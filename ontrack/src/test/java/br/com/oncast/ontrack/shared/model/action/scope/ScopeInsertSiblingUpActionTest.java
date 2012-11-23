package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingUpActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ScopeInsertSiblingUpActionTest extends ModelActionTest {

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

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test
	public void siblingUpMustBeUp() throws UnableToCompleteActionException {
		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingUpAction insertSiblingDownScopeAction = new ScopeInsertSiblingUpAction(firstChild.getId(), newScopeDescription);
		insertSiblingDownScopeAction.execute(context, actionContext);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(lastChild.getParent().getChildren().get(0).getDescription(), newScopeDescription);
		assertEquals(lastChild.getParent().getChildren().get(1), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(2), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantAddSiblingDown() throws UnableToCompleteActionException {
		new ScopeInsertSiblingUpAction(rootScope.getId(), newScopeDescription).execute(context, actionContext);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());

		final ScopeInsertSiblingUpAction insertSiblingDownScopeAction = new ScopeInsertSiblingUpAction(firstChild.getId(), newScopeDescription);
		final ModelAction rollbackAction = insertSiblingDownScopeAction.execute(context, actionContext);

		assertEquals(3, rootScope.getChildren().size());
		assertEquals(lastChild.getParent().getChildren().get(0).getDescription(), newScopeDescription);
		assertEquals(lastChild.getParent().getChildren().get(1), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(2), lastChild);

		rollbackAction.execute(context, actionContext);

		assertEquals(lastChild.getParent().getChildren().get(0), firstChild);
		assertEquals(lastChild.getParent().getChildren().get(1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeInsertSiblingUpActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeInsertSiblingUpAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeInsertSiblingUpAction(new UUID(), "");
	}
}
