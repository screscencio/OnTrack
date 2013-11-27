package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingDownActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
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

public class ScopeInsertSiblingDownActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;
	private String newScopeDescription;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.createScope("root");
		firstChild = ScopeTestUtils.createScope("child");
		lastChild = ScopeTestUtils.createScope("last");

		rootScope.add(firstChild);
		rootScope.add(lastChild);

		newScopeDescription = "description for new scope";

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));
	}

	@Test
	public void siblingDownMustBeDown() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingDownAction insertSiblingDownScopeAction = new ScopeInsertSiblingDownAction(firstChild.getId(), newScopeDescription);
		insertSiblingDownScopeAction.execute(context, actionContext);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(1).getDescription(), newScopeDescription);
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 2), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantAddSiblingDown() throws UnableToCompleteActionException {
		new ScopeInsertSiblingDownAction(rootScope.getId(), "text").execute(context, actionContext);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getParent().getChildIndex(firstChild) + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingDownAction insertSiblingDownScopeAction = new ScopeInsertSiblingDownAction(firstChild.getId(), newScopeDescription);
		final ModelAction rollbackAction = insertSiblingDownScopeAction.execute(context, actionContext);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(1).getDescription(), newScopeDescription);
		assertEquals(firstChild.getParent().getChildren().get(2), lastChild);

		rollbackAction.execute(context, actionContext);

		assertEquals(firstChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeInsertSiblingDownActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeInsertSiblingDownAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeInsertSiblingDownAction(new UUID(), "");
	}

}
