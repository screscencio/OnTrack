package br.com.oncast.ontrack.shared.model.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class UpdateScopeActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("first");
		rootScope.add(firstChild);

		context = new ProjectContext(new Project(rootScope, new Release("")));
	}

	@Test
	public void updateActionChangeScopeDescription() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());
		new ScopeUpdateAction(rootScope.getId(), "new text").execute(context);
		assertEquals("new text", rootScope.getDescription());
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals("root", rootScope.getDescription());

		final ScopeUpdateAction updateScopeAction = new ScopeUpdateAction(rootScope.getId(), "new text");
		final ModelAction rollbackAction = updateScopeAction.execute(context);

		assertEquals("new text", rootScope.getDescription());

		rollbackAction.execute(context);
		assertEquals("root", rootScope.getDescription());
	}
}
