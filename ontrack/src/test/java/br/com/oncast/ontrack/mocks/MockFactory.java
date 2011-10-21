package br.com.oncast.ontrack.mocks;

import br.com.oncast.ontrack.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.project.Project;

public class MockFactory {
	public static Project createProject() {
		return new Project(ScopeTestUtils.getScope2(), ReleaseTestUtils.getRelease());
	}
}
