package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenuShortcutMapping;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;
import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.helper.ScopeTreeMouseHelper;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeShortcutMappings;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ReleaseTag;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;
	private List<HandlerRegistration> registrations;
	private final ScopeTreeMouseHelper mouseHelper;
	private ProjectContext projectContext;
	private final PlanningPlace place;

	public PlanningActivity(final PlanningPlace place) {
		this.place = place;
		activityActionExecutionListener = new ActivityActionExecutionListener();
		mouseHelper = new ScopeTreeMouseHelper();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final TimeTrackingEvent timeTracking = ClientServices.get().metrics().startPlaceLoad(place);
		registrations = new ArrayList<HandlerRegistration>();
		view = new PlanningPanel();
		view.setVisible(false);

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.actionExecution();
		projectContext = ClientServices.getCurrentProjectContext();

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

		view.getApplicationMenu().setProjectName(ClientServices.get().projectRepresentationProvider().getCurrent().getName());
		view.getApplicationMenu().setBackButtonVisibility(false);
		view.getApplicationMenu().clearCustomMenuItems();

		SERVICE_PROVIDER.projectRepresentationProvider().registerProjectListChangeListener(getProjectRepresentationListener());

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);
		view.getSearchBar().setActionExecutionRequestHandler(actionExecutionService);

		view.getScopeTree().setContext(projectContext);
		view.getReleasePanel().setRelease(projectContext.getProjectRelease());

		panel.setWidget(view);
		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.getAlertingMenu());
		registrations.add(ShortcutService.register(view, SERVICE_PROVIDER.actionExecution(), UndoRedoShortCutMapping.values()));
		registrations.add(ShortcutService.register(view, view.getApplicationMenu(), ApplicationMenuShortcutMapping.values()));
		registrations.add(ShortcutService.register(view, this, PlanningShortcutMappings.values()));
		registrations.add(ShortcutService.configureShortcutHelpPanel(view.getAlertingMenu()));

		view.setVisible(true);
		final Release firstReleaseInProgress = getFirstReleaseInProgress(projectContext);
		if (firstReleaseInProgress != null) view.ensureWidgetIsVisible(view.getReleasePanel().getWidgetFor(firstReleaseInProgress));
		view.getScopeTree().setFocus(true);

		registrations.add(registerScopeSelectionEventHandler());

		if (place.getTagId() != null) view.getScopeTree().filterByTag(place.getTagId());

		SERVICE_PROVIDER.applicationState().restore(place.getSelectedScopeId());
		SERVICE_PROVIDER.applicationState().startRecording();

		mouseHelper.register(eventBus, actionExecutionService, view.getScopeTree().getScopeTreeInternalActionHandler(), projectContext, view.getScopeTree()
				.getSelected());

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				timeTracking.end();
			}
		});

	}

	private ProjectListChangeListener getProjectRepresentationListener() {
		return new ProjectListChangeListener() {

			@Override
			public void onProjectNameUpdate(final ProjectRepresentation projectRepresentation) {
				if (!projectContext.getProjectRepresentation().equals(projectRepresentation)) return;

				view.getApplicationMenu().setProjectName(projectRepresentation.getName());
			}

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {}

			@Override
			public void onProjectListAvailabilityChange(final boolean availability) {}
		};
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
		SERVICE_PROVIDER.applicationState().stopRecording();
		SERVICE_PROVIDER.actionExecution().removeActionExecutionListener(activityActionExecutionListener);
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
		projectContext = null;

		for (final HandlerRegistration registration : registrations) {
			registration.removeHandler();
		}
	}

	private HandlerRegistration registerScopeSelectionEventHandler() {
		return ClientServices.get().eventBus().addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			private ScopeCardWidget selectedScope;

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
		if (place.getRequestedProjectId() == null) {
			if (other.place.getRequestedProjectId() != null) return false;
		}
		else if (!place.getRequestedProjectId().equals(other.place.getRequestedProjectId())) return false;
		if (place.getSelectedScopeId() == null) {
			if (other.place.getSelectedScopeId() != null) return false;
		}
		else if (!place.getSelectedScopeId().equals(other.place.getSelectedScopeId())) return false;
		return true;
	}

	public void exportToMindMap() {
		final String url = URLBuilder.buildMindMapExportURL(place.getRequestedProjectId());
		final Element anchor = DOM.createAnchor();
		anchor.setAttribute("href", url);
		anchor.setAttribute("type", "application/xml");
		anchor.setAttribute("download", ClientServices.get().projectRepresentationProvider().getCurrent().getName() + ".mm");
		DOM.appendChild(RootPanel.getBodyElement(), anchor);
		ElementUtils.click(anchor);
		anchor.removeFromParent();
	}

}