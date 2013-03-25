package br.com.oncast.ontrack.client.ui.places.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenuShortcutMapping;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ApplicationMenuItem;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ReleaseSelectionWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanActionSyncController;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanActionSyncController.Display;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanScopeWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseScopeWidget;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ProgressActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private KanbanActionSyncController kanbanActionSyncController;
	private ProgressView view;
	private ProjectContext projectContext;
	private Release release;

	private final List<HandlerRegistration> registrations;

	private final UUID requestedProjectId;
	private final UUID requestedReleaseId;

	private Scope currSelectedScope;

	public ProgressActivity(final ProgressPlace place) {
		requestedProjectId = place.getRequestedProjectId();
		requestedReleaseId = place.getRequestedReleaseId();

		registrations = new ArrayList<HandlerRegistration>();

		try {
			projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(requestedProjectId);
			release = projectContext.findRelease(requestedReleaseId);
			final Kanban kanban = projectContext.getKanban(release);
			view = new ProgressPanel(release, kanban);
			view.getKanbanPanel().setActionExecutionService(SERVICE_PROVIDER.getActionExecutionService());

			kanbanActionSyncController = new KanbanActionSyncController(SERVICE_PROVIDER.getActionExecutionService(), release, new Display() {

				@Override
				public void update() {
					updateViewData();
				}

				@Override
				public void exit() {
					exitToPlanningPlace();
				}

				@Override
				public void updateReleaseInfo() {
					updateCustomApplicationMenus();
				}
			}, ClientServiceProvider.getInstance().getClientErrorMessages());
		}
		catch (final ReleaseNotFoundException e) {
			exitToPlanningPlace();
		}
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		if (view == null) throw new RuntimeException("The view wasnt initialized correctly.");

		view.getApplicationMenu().setProjectName(ClientServiceProvider.getInstance().getProjectRepresentationProvider().getCurrent().getName());
		view.getApplicationMenu().setBackButtonVisibility(true);

		updateViewData();
		updateCustomApplicationMenus();

		panel.setWidget(view);

		kanbanActionSyncController.registerActionExecutionListener();
		view.registerActionExecutionHandler(SERVICE_PROVIDER.getActionExecutionService());

		SERVICE_PROVIDER.getProjectRepresentationProvider().registerProjectListChangeListener(getProjectRepresentationListener());

		registrations.add(ShortcutService.register(RootPanel.get(), view.getApplicationMenu(), ApplicationMenuShortcutMapping.values()));
		registrations.add(ShortcutService.register(RootPanel.get(), SERVICE_PROVIDER.getActionExecutionService(), UndoRedoShortCutMapping.values()));
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.getAlertingPanel());
		registrations.add(ShortcutService.configureShortcutHelpPanel(view.getAlertingPanel()));

		registrations.add(SERVICE_PROVIDER.getActionExecutionService().addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof ScopeRemoveAction && currSelectedScope.getId().equals(action.getReferenceId())) {
					currSelectedScope = null;
					view.getProgressDetailWidget().setSelected(null);
				}
			}
		}));

		registrations.add(ClientServiceProvider.getInstance().getEventBus()
				.addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
					@Override
					public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
						deselectCurrentScopeWidget();
						selectScopeWidget(event.getTargetScope(), event.getSource() instanceof KanbanScopeWidget);
					}
				}));
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

	private void deselectCurrentScopeWidget() {
		setScopeSelection(currSelectedScope, false, false);
	}

	private void selectScopeWidget(final Scope scope, final boolean isKanbanScopeWidget) {
		setScopeSelection(scope, true, isKanbanScopeWidget);
		view.getProgressDetailWidget().setSelected(scope);
		currSelectedScope = scope;
	}

	private void setScopeSelection(final Scope scope, final boolean selection, final boolean isKanbanScopeWidget) {
		if (scope == null) return;

		final ReleaseScopeWidget releaseScopeWidget = view.getReleaseWidget().getWidgetFor(release).getScopeContainer().getWidgetFor(scope);
		if (releaseScopeWidget != null) {
			releaseScopeWidget.setSelected(!isKanbanScopeWidget && selection);

			for (final Scope childTask : scope.getAllLeafs()) {
				final KanbanScopeWidget widget = view.getKanbanPanel().getWidgetFor(childTask);
				if (widget != null) widget.setAssociationHighlight(selection);
			}
		}

		final KanbanScopeWidget kanbanScopeWidget = view.getKanbanPanel().getWidgetFor(scope);
		if (kanbanScopeWidget != null) {
			kanbanScopeWidget.setSelected(isKanbanScopeWidget && selection);
			view.getReleaseWidget().getWidgetFor(release).getScopeContainer().getWidgetFor(getStory(scope)).setAssociationHighlight(selection);
		}
	}

	private Scope getStory(Scope scope) {
		while (scope.getRelease() == null) {
			scope = scope.getParent();
		}
		return scope;
	}

	@Override
	public void onStop() {
		view.unregisterActionExecutionHandler(SERVICE_PROVIDER.getActionExecutionService());
		kanbanActionSyncController.unregisterActionExecutionListener();
		for (final HandlerRegistration registration : registrations) {
			registration.removeHandler();
		}
		SERVICE_PROVIDER.getClientAlertingService().clearAlertingParentWidget();
	}

	protected void updateViewData() {
		view.getKanbanPanel().update(projectContext.getKanban(release));
		view.getProgressDetailWidget().update();
	}

	private void exitToPlanningPlace() {
		final UUID projectId = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrent().getId();
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(new PlanningPlace(projectId));
	}

	private void updateCustomApplicationMenus() {
		view.getApplicationMenu().clearCustomMenuItems().addCustomMenuItem(createReleaseMenuItem(), new ReleaseSelectionWidget());
	}

	private ApplicationMenuItem createReleaseMenuItem() {
		return new ApplicationMenuItem(release.getFullDescription(), true);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ProgressActivity other = (ProgressActivity) obj;
		if (requestedProjectId == null) {
			if (other.requestedProjectId != null) return false;
		}
		else if (!requestedProjectId.equals(other.requestedProjectId)) return false;
		if (requestedReleaseId == null) {
			if (other.requestedReleaseId != null) return false;
		}
		else if (!requestedReleaseId.equals(other.requestedReleaseId)) return false;
		return true;
	}

}