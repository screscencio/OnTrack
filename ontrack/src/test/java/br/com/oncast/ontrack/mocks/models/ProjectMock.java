package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.ReleaseMockFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectMock {
	public static Project getProject() {
		final Project project = new Project(new Scope("Project", new UUID("0")), ReleaseMockFactory.create("proj"));
		return project;
	}

}
