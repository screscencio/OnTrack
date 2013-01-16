package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenuShortcutMapping;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseScopeWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.helper.ScopeTreeMouseHelper;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeShortcutMappings;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ReleaseTag;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;
	private List<HandlerRegistration> registrations;
	private final UUID selectedScopeId;
	private final UUID requestedProjectId;
	private final ScopeTreeMouseHelper mouseHelper;
	private final UUID filteredTagId;

	public PlanningActivity(final PlanningPlace place) {
		requestedProjectId = place.getRequestedProjectId();
		selectedScopeId = place.getSelectedScopeId();
		filteredTagId = place.getTagId();
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
		activityActionExecutionListener = new ActivityActionExecutionListener();
		mouseHelper = new ScopeTreeMouseHelper();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		registrations = new ArrayList<HandlerRegistration>();
		view = new PlanningPanel();
		view.setVisible(false);

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
		final ProjectContext projectContext = ClientServiceProvider.getCurrentProjectContext();

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

		view.getApplicationMenu().setProjectName(projectContext.getProjectRepresentation().getName());
		view.getApplicationMenu().setBackButtonVisibility(false);
		view.getApplicationMenu().clearCustomMenuItems();

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);

		view.getScopeTree().setContext(projectContext);
		view.getReleasePanel().setRelease(projectContext.getProjectRelease());

		panel.setWidget(view);
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.getAlertingMenu());
		registrations.add(ShortcutService.register(view, SERVICE_PROVIDER.getActionExecutionService(), UndoRedoShortCutMapping.values()));
		registrations.add(ShortcutService.register(view, view.getApplicationMenu(), ApplicationMenuShortcutMapping.values()));
		registrations.add(ShortcutService.register(view, this, PlanningShortcutMappings.values()));
		registrations.add(ShortcutService.configureShortcutHelpPanel(view.getAlertingMenu()));

		view.setVisible(true);
		final Release firstReleaseInProgress = getFirstReleaseInProgress(projectContext);
		if (firstReleaseInProgress != null) view.ensureWidgetIsVisible(view.getReleasePanel().getWidgetFor(firstReleaseInProgress));
		view.getScopeTree().setFocus(true);

		registrations.add(registerScopeSelectionEventHandler());
		registrations.add(registerScopeImpedimentUpdateEventHandler());

		if (filteredTagId != null) view.getScopeTree().getScopeTreeInternalActionHandler().filterByTag(filteredTagId);

		SERVICE_PROVIDER.getClientApplicationStateService().restore(selectedScopeId);
		SERVICE_PROVIDER.getClientApplicationStateService().startRecording();

		mouseHelper.register(eventBus, actionExecutionService, view.getScopeTree().getScopeTreeInternalActionHandler(), projectContext, view.getScopeTree()
				.getSelected());

		SERVICE_PROVIDER.getClientMetricService().onBrowserLoadEnd();
	}

	private Release getFirstReleaseInProgress(final ProjectContext context) {
		for (final Release release : context.getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (!release.isDone() && release.getEffortSum() > 0) return release;
		}
		return null;
	}

	@Override
	public void onStop() {
		mouseHelper.unregister();
		SERVICE_PROVIDER.getClientApplicationStateService().stopRecording();
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
		SERVICE_PROVIDER.getClientAlertingService().clearAlertingParentWidget();

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

				final ReleaseScopeWidget scopeWidget = releaseWidget.getScopeContainer().getWidgetFor(scope);
				scopeWidget.setHasOpenImpediments(event.hasOpenImpediments());
			}

		});
	}

	private HandlerRegistration registerScopeSelectionEventHandler() {
		return ClientServiceProvider.getInstance().getEventBus().addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			private ReleaseScopeWidget selectedScope;

			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				if (selectedScope != null) selectedScope.setSelected(false);

				final Scope scope = event.getTargetScope();
				final Release release = scope.getRelease();
				if (release == null) return;

				final ReleaseWidget releaseWidget = view.getReleasePanel().getWidgetFor(release);

				selectedScope = releaseWidget.getScopeContainer().getWidgetFor(scope);
				selectedScope.setSelected(true);

				if (event.getSource() instanceof ReleaseTag || event.getSource() instanceof ScopeTreeShortcutMappings) {
					releaseWidget.setHierarchicalContainerState(true);
					view.ensureWidgetIsVisible(selectedScope);
				}
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final PlanningActivity other = (PlanningActivity) obj;
		if (requestedProjectId == null) {
			if (other.requestedProjectId != null) return false;
		}
		else if (!requestedProjectId.equals(other.requestedProjectId)) return false;
		if (selectedScopeId == null) {
			if (other.selectedScopeId != null) return false;
		}
		else if (!selectedScopeId.equals(other.selectedScopeId)) return false;
		return true;
	}
}