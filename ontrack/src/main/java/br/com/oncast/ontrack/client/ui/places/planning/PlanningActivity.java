package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionManager;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private Project mockedProject;
	private ProjectContext mockedProjectContext;

	private final class PlanningActionExecutionRequestHandler implements ActionExecutionRequestHandler, ActionExecutionListener {

		private final ActionManager actionManager;
		private final List<ActionExecutionListener> actionExecutionSuccessHandlers;

		public PlanningActionExecutionRequestHandler(final List<ActionExecutionListener> actionExecutionSuccessHandlers) {
			actionManager = new ActionManager(this);
			this.actionExecutionSuccessHandlers = actionExecutionSuccessHandlers;
		}

		@Override
		public void onActionExecutionRequest(final ScopeAction action) {
			actionManager.execute(action, getProjectContext());
		}

		@Override
		public void onActionUndoRequest() {
			actionManager.undo(getProjectContext());
		}

		@Override
		public void onActionRedoRequest() {
			actionManager.redo(getProjectContext());
		}

		@Override
		public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
			for (final ActionExecutionListener handler : actionExecutionSuccessHandlers) {
				handler.onActionExecution(action, wasRollback);
			}
		}
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final PlanningView view = new PlanningPanel();
		view.setActionExecutionRequestHandler(new PlanningActionExecutionRequestHandler(view.getActionExecutionSuccessListeners()));

		final Project project = getProject();
		view.setScope(project.getScope());
		view.setReleases(project.getProjectRelease().getChildReleases());

		panel.setWidget(view);
	}

	public ProjectContext getProjectContext() {
		if (mockedProjectContext != null) return mockedProjectContext;
		return mockedProjectContext = new ProjectContext(getProject());
	}

	// TODO Get initial model data from server
	private Project getProject() {
		if (mockedProject != null) return mockedProject;
		return mockedProject = ProjectMockFactory.createProjectMock();
	}
}
