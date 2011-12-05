package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectTestUtils {

	public static final String DEFAULT_PROJECT_NAME = "Default project";

	public static Project createProject() {
		return createProject(getDefaultProjectRepresentation(), getDefaultScope(), getDefaultRelease());
	}

	public static Project createProject(final Scope scope, final Release release) {
		return createProject(getDefaultProjectRepresentation(), scope, release);
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

	public static ProjectRepresentation createProjectRepresentation() {
		return getDefaultProjectRepresentation();
	}

	public static ProjectRepresentation createRepresentation(final long projectId) {
		return createProjectRepresentation(projectId, DEFAULT_PROJECT_NAME);
	}

	public static ProjectRepresentation createProjectRepresentation(final long projectId, final String projectName) {
		return new ProjectRepresentation(projectId, projectName);
	}

	public static ProjectRepresentation createProjectRepresentation(final String projectName) {
		return new ProjectRepresentation(projectName);
	}

	private static ProjectRepresentation getDefaultProjectRepresentation() {
		return createRepresentation(1);
	}

	private static Scope getDefaultScope() {
		return new Scope(getDefaultProjectRepresentation().getName(), new UUID("0"));
	}

	private static Release getDefaultRelease() {
		return new Release(getDefaultProjectRepresentation().getName(), new UUID("release0"));
	}

	public static List<ProjectRepresentation> createProjectRepresentationList(final int size) {
		final List<ProjectRepresentation> list = new ArrayList<ProjectRepresentation>();
		return list;
	}

	public static ProjectAuthorization createAuthorization() {
		return new ProjectAuthorization(UserTestUtils.createUser(), createProjectRepresentation());
	}

}
