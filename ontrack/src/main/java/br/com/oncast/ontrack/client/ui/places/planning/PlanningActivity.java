package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.mocks.ProjectMockFactory;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private Project mockedProject;
	private ProjectContext mockedProjectContext;

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final PlanningView view = new PlanningPanel();
		view.setActionExecutionRequestHandler(new PlanningActionExecutionRequestHandler(getProjectContext(), view.getActionExecutionSuccessListeners()));

		final Project project = getProject();
		view.setScope(project.getScope());
		view.setRelease(project.getProjectRelease());

		panel.setWidget(view);
	}

	private ProjectContext getProjectContext() {
		if (mockedProjectContext != null) return mockedProjectContext;
		return mockedProjectContext = new ProjectContext(getProject());
	}

	// TODO Get initial model data from server
	private Project getProject() {
		if (mockedProject != null) return mockedProject;
		return mockedProject = ProjectMockFactory.createProjectMock();
	}
}
