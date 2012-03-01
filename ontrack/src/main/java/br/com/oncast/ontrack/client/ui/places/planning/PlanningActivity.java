package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.BreadcrumbWidget;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;

	public PlanningActivity() {
		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		view = new PlanningPanel();
		view.setVisible(false);

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
		final ProjectRepresentation currentProjectRepresentation = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrent();
		final long currentProjectId = currentProjectRepresentation.getId();
		final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(currentProjectId);

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

		view.getApplicationMenu().setProjectName(projectContext.getProjectRepresentation().getName());

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);

		view.getScopeTree().setContext(projectContext);
		view.getReleasePanel().setRelease(projectContext.getProjectRelease());

		// FIXME Matsumoto enable scroll when art where applied
		// ClientServiceProvider.getInstance().getEventBus().addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
		// @Override
		// public void onScopeSelectionRequest(final Scope scope) {
		// final Release release = scope.getRelease();
		// if (release == null) return;
		//
		// view.ensureWidgetIsVisible(view.getReleasePanel().findWidgetAndSetContainerState(release, true));
		// }
		//
		// @Override
		// public boolean mustIgnoreFromSource(final Object source) {
		// return source instanceof ScopeWidget;
		// }
		// });
		//
		updateBreadcrumb(currentProjectRepresentation);

		panel.setWidget(view);
		ShortcutService.register(view, SERVICE_PROVIDER.getActionExecutionService(), UndoRedoShortCutMapping.values());
		ShortcutService.register(view, this, PlanningShortcutMappings.values());
		view.setVisible(true);
		view.getScopeTree().setFocus(true);
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners(final PlanningView view) {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}

	public void showSearchScope() {
		view.getScopeTree().showSearchWidget();
	}

	private void updateBreadcrumb(final ProjectRepresentation project) {
		final BreadcrumbWidget breadcrumb = view.getApplicationMenu().getBreadcrumb();
		breadcrumb.clearItems();
	}
}