package br.com.oncast.ontrack.mocks;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class MockFactory {
	public static Project createProject() {
		return new Project(getScope(), getProjectRelease());
	}

	private static Release getProjectRelease() {
		return ReleaseMock.getRelease();
	}

	private static Scope getScope() {
		return ScopeMock.getScope2();
	}
}
