package br.com.oncast.ontrack.shared.model.action.release;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseScopeUpdatePriorityActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ReleaseScopeUpdatePriorityActionTest extends ModelActionTest {
	private ProjectContext context;
	private Release release;
	private Scope rootScope;
	private Scope firstChild;
	private Scope secondChild;
	private Scope thirdChild;

	@Before
	public void up() {
		firstChild = ScopeTestUtils.createScope("firstChild");
		secondChild = ScopeTestUtils.createScope("secondChild");
		thirdChild = ScopeTestUtils.createScope("thirdChild");
		rootScope = ScopeTestUtils.createScope("rootScope");
		rootScope.add(firstChild);
		rootScope.add(secondChild);
		rootScope.add(thirdChild);
		release = new Release("release", new UUID());
		release.addScope(firstChild);
		release.addScope(secondChild);
		release.addScope(thirdChild);

		context = ProjectTestUtils.createProjectContext(rootScope, release);
	}

	@Test
	public void decresePriorityOfTheSecondChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) + 1);
		increase.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
	}

	@Test
	public void decresePriorityOfTheFirstChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), firstChild.getId(),
				release.getScopeIndex(firstChild) + 1);
		increase.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void decresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction decrease = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) + 1);
		final ModelAction increase = decrease.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());

		increase.execute(context, Mockito.mock(ActionContext.class));
		assertEquals(secondChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void decreseLowestPriorityScope() throws Exception {
		new ReleaseScopeUpdatePriorityAction(release.getId(), thirdChild.getId(),
				release.getScopeIndex(thirdChild) + 1).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void decreseScopePriorityOutsideRelease() throws Exception {
		final Scope scopeOutsideRelease = ScopeTestUtils.createScope("otherScope");
		new ReleaseScopeUpdatePriorityAction(release.getId(), scopeOutsideRelease.getId(),
				release.getScopeIndex(scopeOutsideRelease) + 1).execute(context, Mockito.mock(ActionContext.class));
	}

	@Test
	public void incresePriorityOfTheSecondChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) - 1);
		increase.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheThirdChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), thirdChild.getId(),
				release.getScopeIndex(thirdChild) - 1);
		increase.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) - 1);
		final ReleaseScopeUpdatePriorityAction decrease = (ReleaseScopeUpdatePriorityAction) increase.execute(context, Mockito.mock(ActionContext.class));

		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());

		decrease.execute(context, Mockito.mock(ActionContext.class));
		assertEquals(secondChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void increseTheMostPriorityScope() throws Exception {
		new ReleaseScopeUpdatePriorityAction(release.getId(), firstChild.getId(),
				release.getScopeIndex(firstChild) - 1).execute(context, Mockito.mock(ActionContext.class));
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseScopeUpdatePriorityActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseScopeUpdatePriorityAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ReleaseScopeUpdatePriorityAction(new UUID(), new UUID(), 2);
	}

}
