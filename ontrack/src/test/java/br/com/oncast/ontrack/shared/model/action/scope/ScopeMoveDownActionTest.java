package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveDownActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ScopeMoveDownActionTest extends ModelActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.createScope("root");
		firstChild = ScopeTestUtils.createScope("child");
		lastChild = ScopeTestUtils.createScope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedDown() throws UnableToCompleteActionException {
		new ScopeMoveDownAction(rootScope.getId()).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test
	public void movedDownScopeMustBeDown() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveDownAction moveDown = new ScopeMoveDownAction(firstChild.getId());
		moveDown.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveDownAction moveDown = new ScopeMoveDownAction(firstChild.getId());
		final ModelAction rollbackAction = moveDown.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);

		rollbackAction.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void lastNodeCantBeMovedDown() throws UnableToCompleteActionException {
		new ScopeMoveDownAction(lastChild.getId()).execute(context, Mockito.mock(ActionContext.class));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeMoveDownActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeMoveDownAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeMoveDownAction(new UUID());
	}

}
