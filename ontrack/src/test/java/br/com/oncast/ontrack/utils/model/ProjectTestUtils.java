package br.com.oncast.ontrack.utils.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectTestUtils {

	public static final String DEFAULT_PROJECT_NAME = "Default project";

	public static Project createProject() {
		return createProject(getDefaultScope(), getDefaultRelease());
	}

	public static Project createProject(final Scope scope, final Release release) {
		return createProject(getDefaultRepresentation(), scope, release);
	}

	public static Project createProject(final ProjectRepresentation projectRepresentation, final Scope scope, final Release release, final Set<User> userList) {
		final Project project = new Project(projectRepresentation, scope, release);
		project.setUserList(userList);
		return project;
	}

	public static ProjectContext createProjectContext() {
		return new ProjectContext(createProject());
	}

	public static ProjectContext createProjectContext(final Scope scope, final Release release) {
		return new ProjectContext(createProject(scope, release));
	}

	public static ProjectRepresentation createRepresentation() {
		return getDefaultRepresentation();
	}

	public static ProjectRepresentation createRepresentation(final UUID projectId) {
		return createRepresentation(projectId, DEFAULT_PROJECT_NAME);
	}

	public static ProjectRepresentation createRepresentation(final UUID projectId, final String projectName) {
		return new ProjectRepresentation(projectId, projectName);
	}

	public static ProjectRepresentation createRepresentation(final String projectName) {
		return new ProjectRepresentation(projectName);
	}

	private static ProjectRepresentation getDefaultRepresentation() {
		return createRepresentation(new UUID());
	}

	private static Scope getDefaultScope() {
		return ScopeTestUtils.createScope(getDefaultRepresentation().getName(), UUID.INVALID_UUID);
	}

	private static Release getDefaultRelease() {
		return new Release(getDefaultRepresentation().getName(), new UUID("release0"));
	}

	public static List<ProjectRepresentation> createRepresentationList(final int size) {
		final List<ProjectRepresentation> list = new ArrayList<ProjectRepresentation>();
		return list;
	}

	public static ProjectAuthorization createAuthorization() throws Exception {
		return new ProjectAuthorization(UserTestUtils.createUser(), createRepresentation());
	}

	public static ProjectAuthorization createAuthorization(final User user) {
		return new ProjectAuthorization(user, createRepresentation());
	}

	public static List<ProjectAuthorization> createAuthorizations(final int numberOfAuthorizations, final User user) {
		final List<ProjectAuthorization> auths = new ArrayList<ProjectAuthorization>();
		for (int i = 0; i < numberOfAuthorizations; i++) {
			auths.add(createAuthorization(user));
		}
		return auths;
	}

	public static Project createPopulatedProject() {
		final Release projectRelease = ReleaseTestUtils.getRelease();
		final Release r1 = projectRelease.getChild(0);

		final Scope scope = ScopeTestUtils.getSimpleScope();
		r1.addScope(scope.getChild(0));
		r1.addScope(scope.getChild(1));
		r1.addScope(scope.getChild(2));

		for (final Release r : r1.getChildren()) {
			final Scope s = ScopeTestUtils.getSimpleScope();
			r.addScope(s.getChild(0));
			r.addScope(s.getChild(1));
			r.addScope(s.getChild(2));
		}

		return createProject(getDefaultRepresentation(), scope, projectRelease);
	}

	public static ProjectContext createProjectContext(final Scope scope, final Release release, final Set<User> userList) {
		return new ProjectContext(createProject(scope, release, userList));
	}

	private static Project createProject(final Scope scope, final Release release, final Set<User> userList) {
		return createProject(getDefaultRepresentation(), scope, release, userList);
	}

	public static Project createProject(final ProjectRepresentation projectRepresentation, final Scope scope, final Release release) {
		final HashSet<User> userList = new HashSet<User>();
		userList.add(UserTestUtils.getAdmin());
		return createProject(projectRepresentation, scope, release, userList);
	}
}
