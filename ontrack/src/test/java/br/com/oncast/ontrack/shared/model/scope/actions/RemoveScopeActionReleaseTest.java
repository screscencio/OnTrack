package br.com.oncast.ontrack.shared.model.scope.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class RemoveScopeActionReleaseTest {

	private Scope rootScope;
	private ProjectContext context;
	private Release release;

	@Before
	public void setUp() {
		rootScope = ScopeMock.getScope();
		release = ReleaseMock.getRelease().getChildReleases().get(0);
		context = new ProjectContext(new Project(rootScope, release));
	}

	@Test
	public void shouldRemoveScopeOfRelease() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(1);
		release.addScope(removedScope);
		removedScope.setRelease(release);

		new ScopeRemoveAction(removedScope.getId()).execute(context);

		assertNull(removedScope.getRelease());
		assertEquals(0, release.getScopeList().size());
	}

}
