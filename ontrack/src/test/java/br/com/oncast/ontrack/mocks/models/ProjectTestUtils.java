package br.com.oncast.ontrack.mocks.models;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectTestUtils {
	private static final ProjectRepresentation DEFAULT_PROJECT_REPRESENTATION = new ProjectRepresentation(1,
			"Default Project");
	private static Release DEFAULT_RELEASE = new Release("proj", new UUID("release0"));
	private static Scope DEFAULT_SCOPE = new Scope("Project", new UUID("0"));

	public static Project createProject() {
		return createProject(DEFAULT_PROJECT_REPRESENTATION, DEFAULT_SCOPE, DEFAULT_RELEASE);
	}

	public static Project createProject(final Scope scope, final Release release) {
		return createProject(DEFAULT_PROJECT_REPRESENTATION, scope, release);
	}

	public static Project createProject(final ProjectRepresentation projectRepresentation, final Scope scope, final Release release) {
		final Project project = new Project(projectRepresentation, scope, release);
		return project;
	}

	public static ProjectContext createProjectContext() {
		return new ProjectContext(createProject());
	}

	public static ProjectContext createProjectContext(final Scope scope, final Release release) {
		return new ProjectContext(createProject(scope, release));
	}
}
