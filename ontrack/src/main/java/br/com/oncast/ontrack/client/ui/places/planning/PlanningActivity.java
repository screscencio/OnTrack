package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;
	private List<HandlerRegistration> registrations;

	public PlanningActivity() {
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		registrations = new ArrayList<HandlerRegistration>();
		view = new PlanningPanel();
		view.setVisible(false);

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
		final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getCurrentProjectContext();

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

		view.getApplicationMenu().setProjectName(projectContext.getProjectRepresentation().getName());
		view.getApplicationMenu().setBackButtonVisibility(false);
		view.getApplicationMenu().clearCustomMenuItems();

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);
		view.getSearchBar().setActionExecutionRequestHandler(actionExecutionService);

		view.getScopeTree().setContext(projectContext);
		view.getReleasePanel().setRelease(projectContext.getProjectRelease());

		panel.setWidget(view);
		SERVICE_PROVIDER.getClientNotificationService().setNotificationParentWidget(view.getNotificationMenu());
		registrations.add(ShortcutService.register(view, SERVICE_PROVIDER.getActionExecutionService(), UndoRedoShortCutMapping.values()));
		registrations.add(ShortcutService.register(view, this, PlanningShortcutMappings.values()));

		view.setVisible(true);
		final Release firstReleaseInProgress = getFirstReleaseInProgress(projectContext);
		if (firstReleaseInProgress != null) view.ensureWidgetIsVisible(view.getReleasePanel().getWidgetFor(firstReleaseInProgress));
		view.getScopeTree().setFocus(true);

		registrations.add(registerScopeSelectionEventHandler());
		registrations.add(registerScopeImpedimentUpdateEventHandler());
		SERVICE_PROVIDER.getClientApplicationStateService().restore();
		SERVICE_PROVIDER.getClientApplicationStateService().startRecording();

		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadEnd();
	}

	private Release getFirstReleaseInProgress(final ProjectContext context) {
		for (final Release release : context.getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (!release.isDone() && release.getEffortSum() > 0) return release;
		}
		return null;
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientApplicationStateService().stopRecording();
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
		SERVICE_PROVIDER.getClientNotificationService().clearNotificationParentWidget();

		for (final HandlerRegistration registration : registrations) {
			registration.removeHandler();
		}
	}

	private HandlerRegistration registerScopeImpedimentUpdateEventHandler() {
		return ClientServiceProvider.getInstance().getEventBus().addHandler(ScopeDetailUpdateEvent.getType(), new ScopeDetailUpdateEventHandler() {
			@Override
			public void onScopeDetailUpdate(final ScopeDetailUpdateEvent event) {
				final Scope scope = event.getTargetScope();
				final Release release = scope.getRelease();
				if (release == null) return;

				final ReleaseWidget releaseWidget = view.getReleasePanel().getWidgetFor(release);

				final ScopeWidget scopeWidget = releaseWidget.getScopeContainer().getWidgetFor(scope);
				scopeWidget.setHasOpenImpediments(event.hasOpenImpediments());
			}

		});
	}

	private HandlerRegistration registerScopeSelectionEventHandler() {
		return ClientServiceProvider.getInstance().getEventBus().addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			private ScopeWidget selectedScope;

			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				if (selectedScope != null) selectedScope.setSelected(false);

				final Scope scope = event.getTargetScope();
				final Release release = scope.getRelease();
				if (release == null) return;

				final ReleaseWidget releaseWidget = view.getReleasePanel().getWidgetFor(release);

				selectedScope = releaseWidget.getScopeContainer().getWidgetFor(scope);
				selectedScope.setSelected(true);

				if (event.getSource() instanceof ScopeWidget) return;

				releaseWidget.setHierarchicalContainerState(true);
				view.ensureWidgetIsVisible(selectedScope);
			}

		});
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners(final PlanningView view) {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}

	public void showSearchScope() {
		view.getSearchBar().focus();
	}

	public void toggleReleasePanel() {
		view.toggleReleasePanel();
	}
}