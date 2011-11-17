package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseScopeUpdatePriorityActionTest {
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
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(secondChild.getId(),
				release.getScopeIndex(secondChild) + 1);
		increase.execute(context);

		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
	}

	@Test
	public void decresePriorityOfTheFirstChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(firstChild.getId(),
				release.getScopeIndex(firstChild) + 1);
		increase.execute(context);

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void decresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction decrease = new ReleaseScopeUpdatePriorityAction(secondChild.getId(),
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
		new ReleaseScopeUpdatePriorityAction(thirdChild.getId(),
				release.getScopeIndex(thirdChild) + 1).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void decreseScopePriorityOutsideRelease() throws Exception {
		final Scope scopeOutsideRelease = new Scope("otherScope");
		new ReleaseScopeUpdatePriorityAction(scopeOutsideRelease.getId(),
				release.getScopeIndex(scopeOutsideRelease) + 1).execute(context);
	}

	@Test
	public void incresePriorityOfTheSecondChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(secondChild.getId(),
				release.getScopeIndex(secondChild) - 1);
		increase.execute(context);

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheThirdChild() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(thirdChild.getId(),
				release.getScopeIndex(thirdChild) - 1);
		increase.execute(context);

		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ReleaseScopeUpdatePriorityAction increase = new ReleaseScopeUpdatePriorityAction(secondChild.getId(),
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
		new ReleaseScopeUpdatePriorityAction(firstChild.getId(),
				release.getScopeIndex(firstChild) - 1).execute(context);
	}

}
