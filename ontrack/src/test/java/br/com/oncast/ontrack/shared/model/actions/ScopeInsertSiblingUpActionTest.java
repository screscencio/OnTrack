package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ScopeInsertSiblingUpActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;
	private String newScopeDescription;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("child");
		lastChild = new Scope("last");

		rootScope.add(firstChild);
		rootScope.add(lastChild);

		newScopeDescription = "description for new scope";

		context = new ProjectContext(new Project(rootScope, ReleaseFactoryTestUtil.create("")));
	}

	@Test
	public void siblingUpMustBeUp() throws UnableToCompleteActionException {
		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingUpAction insertSiblingDownScopeAction = new ScopeInsertSiblingUpAction(firstChild.getId(), newScopeDescription);
		insertSiblingDownScopeAction.execute(context);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(lastChild.getParent().getChildren().get(0).getDescription(), newScopeDescription);
		assertEquals(lastChild.getParent().getChildren().get(1), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(2), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantAddSiblingDown() throws UnableToCompleteActionException {
		new ScopeInsertSiblingUpAction(rootScope.getId(), newScopeDescription).execute(context);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingUpAction insertSiblingDownScopeAction = new ScopeInsertSiblingUpAction(firstChild.getId(), newScopeDescription);
		final ModelAction rollbackAction = insertSiblingDownScopeAction.execute(context);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(lastChild.getParent().getChildren().get(0).getDescription(), newScopeDescription);
		assertEquals(lastChild.getParent().getChildren().get(1), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(2), lastChild);

		rollbackAction.execute(context);

		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
	}

}
