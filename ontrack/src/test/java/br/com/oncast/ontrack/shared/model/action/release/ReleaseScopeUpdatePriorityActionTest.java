package br.com.oncast.ontrack.shared.model.action.release;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseScopeUpdatePriorityActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ReleaseScopeUpdatePriorityActionTest extends ModelActionTest {
	private ProjectContext context;
	private Release release;
	private Scope rootScope;
	private Scope firstChild;
	private Scope secondChild;
	private Scope thirdChild;

	@Before
	public void up() {
		firstChild = new Scope("firstChild");
		secondChild = new Scope("secondChild");
		thirdChild = new Scope("thirdChild");
		rootScope = new Scope("rootScope");
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
		increase.execute(context);

		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
	}

	@Test
	public void decresePriorityOfTheFirstChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), firstChild.getId(),
				release.getScopeIndex(firstChild) + 1);
		increase.execute(context);

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void decresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction decrease = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) + 1);
		final ModelAction increase = decrease.execute(context);

		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());

		increase.execute(context);
		assertEquals(secondChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void decreseLowestPriorityScope() throws Exception {
		new ReleaseScopeUpdatePriorityAction(release.getId(), thirdChild.getId(),
				release.getScopeIndex(thirdChild) + 1).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void decreseScopePriorityOutsideRelease() throws Exception {
		final Scope scopeOutsideRelease = new Scope("otherScope");
		new ReleaseScopeUpdatePriorityAction(release.getId(), scopeOutsideRelease.getId(),
				release.getScopeIndex(scopeOutsideRelease) + 1).execute(context);
	}

	@Test
	public void incresePriorityOfTheSecondChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) - 1);
		increase.execute(context);

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheThirdChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), thirdChild.getId(),
				release.getScopeIndex(thirdChild) - 1);
		increase.execute(context);

		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(release.getId(), secondChild.getId(),
				release.getScopeIndex(secondChild) - 1);
		final ReleaseScopeUpdatePriorityAction decrease = (ReleaseScopeUpdatePriorityAction) increase.execute(context);

		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());

		decrease.execute(context);
		assertEquals(secondChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void increseTheMostPriorityScope() throws Exception {
		new ReleaseScopeUpdatePriorityAction(release.getId(), firstChild.getId(),
				release.getScopeIndex(firstChild) - 1).execute(context);
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
	protected ModelAction getInstance() {
		return new ReleaseScopeUpdatePriorityAction(new UUID(), new UUID(), 2);
	}

}
