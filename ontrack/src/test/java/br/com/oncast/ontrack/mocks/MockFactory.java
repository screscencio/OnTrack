package br.com.oncast.ontrack.mocks;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;

public class MockFactory {
	public static Project createProject() {
		return new Project(ScopeMock.getScope2(), ReleaseMock.getRelease());
	}
}
