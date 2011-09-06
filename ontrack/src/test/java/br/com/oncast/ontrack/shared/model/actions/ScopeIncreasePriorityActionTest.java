package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeIncreasePriorityActionTest {

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

		context = new ProjectContext(new Project(rootScope, release));
	}

	@Test
	public void incresePriorityOfTheSecondChild() throws Exception {
		final ScopeIncreasePriorityAction increase = new ScopeIncreasePriorityAction(release.getId(), secondChild.getId());
		increase.execute(context);

		assertEquals(firstChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(thirdChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheThirdChild() throws Exception {
		final ScopeIncreasePriorityAction increase = new ScopeIncreasePriorityAction(release.getId(), thirdChild.getId());
		increase.execute(context);

		assertEquals(thirdChild.getDescription(), release.getScopeList().get(1).getDescription());
		assertEquals(firstChild.getDescription(), release.getScopeList().get(0).getDescription());
		assertEquals(secondChild.getDescription(), release.getScopeList().get(2).getDescription());
	}

	@Test
	public void incresePriorityOfTheSecondChildAndRollbackTheAction() throws Exception {
		final ScopeIncreasePriorityAction increase = new ScopeIncreasePriorityAction(release.getId(), secondChild.getId());
		final ScopeDecreasePriorityAction decrease = (ScopeDecreasePriorityAction) increase.execute(context);

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
		new ScopeIncreasePriorityAction(release.getId(), firstChild.getId()).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void increseScopePriorityOutsideRelease() throws Exception {
		final Scope scopeOutsideRelease = new Scope("otherScope");
		new ScopeIncreasePriorityAction(release.getId(), scopeOutsideRelease.getId()).execute(context);
	}
}
