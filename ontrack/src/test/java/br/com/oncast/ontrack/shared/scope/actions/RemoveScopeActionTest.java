package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

// TODO Create tests that check if the releases of removed childs are updated.
public class RemoveScopeActionTest {

	private Scope rootScope;
	private Scope child1Level1;
	private Scope child2Level1;
	private ProjectContext context;
	private Scope child1Level2;
	private Scope child1Level3;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		child1Level1 = new Scope("child1Level1");
		child2Level1 = new Scope("child2Level1");
		child1Level2 = new Scope("child1Level2");
		child1Level3 = new Scope("child2Level2");
		rootScope.add(child1Level1);
		rootScope.add(child2Level1);
		child1Level1.add(child1Level2);
		child1Level2.add(child1Level3);

		context = new ProjectContext(new Project(rootScope, new Release("")));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootScopeCantBeRemoved() throws UnableToCompleteActionException {
		new ScopeRemoveAction(rootScope.getId()).execute(context);
	}

	@Test
	public void aRemovedScopeMustBeRemovedFromFather() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));

		new ScopeRemoveAction(child2Level1.getId()).execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child2Level1.getId());
		removeScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));

		removeScopeAction.rollback(context);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
	}

	@Test
	public void executeMustRemoveChildsFromRemovedScope() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		removeScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);
	}

	@Test
	public void rollbackMustRevertChildsFromRemovedScope() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		removeScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);

		removeScopeAction.rollback(context);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

	}

	@Test
	public void executeARolledbackActionMustRemoveChildScopes() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		final ScopeRemoveAction removeScopeAction = new ScopeRemoveAction(child1Level1.getId());
		removeScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);

		removeScopeAction.rollback(context);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(child1Level1, rootScope.getChildren().get(0));
		assertEquals(child2Level1, rootScope.getChildren().get(1));
		assertEquals(child1Level2, rootScope.getChildren().get(0).getChildren().get(0));
		assertEquals(child1Level3, rootScope.getChildren().get(0).getChildren().get(0).getChildren().get(0));

		removeScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(child2Level1, rootScope.getChildren().get(0));
		assertNull(child1Level1.getParent());
		assertNull(child1Level2.getParent());
		assertNull(child1Level3.getParent());
		assertEquals(child1Level1.getChildren().size(), 0);
		assertEquals(child1Level2.getChildren().size(), 0);
		assertEquals(child1Level3.getChildren().size(), 0);
	}
}
