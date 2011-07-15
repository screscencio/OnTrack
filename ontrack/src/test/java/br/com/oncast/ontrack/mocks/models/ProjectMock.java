package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectMock {
	public static Project getProject() {
		final Project project = new Project(new Scope("Project", new UUID("0")), new Release("proj"));
		// TODO Remove this when "Example Scope" is not used anymore to mock an initial scope in persistence layer.
		// project.getProjectScope().add(new Scope("Example Scope", new UUID("1")));

		return project;
	}

}
