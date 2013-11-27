package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveLeftActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScopeMoveLeftActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope middle;
	private Scope lastChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.createScope("root");
		middle = ScopeTestUtils.createScope("middle");
		lastChild = ScopeTestUtils.createScope("last");
		rootScope.add(middle);
		middle.add(lastChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedLeft() throws UnableToCompleteActionException {
		new ScopeMoveLeftAction(rootScope.getId()).execute(context, actionContext);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void aRootChildCantbeMovedLeft() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), middle);
		new ScopeMoveLeftAction(middle.getId()).execute(context, actionContext);
	}

	@Test
	public void aChildMovedLeftMustChangeToASibling() throws UnableToCompleteActionException {
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());

		new ScopeMoveLeftAction(lastChild.getId()).execute(context, actionContext);

		assertEquals(0, middle.getChildren().size());
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(middle, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());

		final ScopeMoveLeftAction moveLeftScopeAction = new ScopeMoveLeftAction(lastChild.getId());
		final ModelAction rollbackAction = moveLeftScopeAction.execute(context, actionContext);

		assertEquals(0, middle.getChildren().size());
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(middle, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));

		rollbackAction.execute(context, actionContext);

		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeMoveLeftActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeMoveLeftAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeMoveLeftAction(new UUID());
	}
}
