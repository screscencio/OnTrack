package br.com.oncast.ontrack.shared.model.action.release;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseCreateActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ReleaseCreateActionTest extends ModelActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test
	public void shouldCreateNewReleaseInsideProjectReleaseWhenNoRelativeReleaseIsProvided() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("New release");
		ActionTestUtils.execute(createAction, context);

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("New release", rootRelease.getChild(3).getDescription());
	}

	@Test
	public void shouldCreateEntireReleaseHierarchy() throws UnableToCompleteActionException {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("R4/It1/w1/d1");
		ActionTestUtils.execute(createAction, context);

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChild(0).getChildren().size());
		assertEquals("w1", rootRelease.getChild(3).getChild(0).getChild(0).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChild(0).getChild(0).getChildren().size());
		assertEquals("d1", rootRelease.getChild(3).getChild(0).getChild(0).getChild(0).getDescription());
	}

	@Test
	public void shouldCreateOnlySubReleaseWhenParentReleaseAlreadyExists() throws UnableToCompleteActionException {
		final Release releaseR2 = rootRelease.getChild(1);

		final ReleaseCreateAction createAction = new ReleaseCreateAction("R2/It5");
		ActionTestUtils.execute(createAction, context);

		// Do not create a new release for 'R2', because it already exists.
		assertEquals(3, rootRelease.getChildren().size());
		assertEquals("R2", releaseR2.getDescription());
		// Create a new release called 'It5' inside 'R2'
		assertEquals(2, releaseR2.getChildren().size());
		assertEquals("It5", releaseR2.getChild(1).getDescription());
	}

	@Test
	public void shouldCreateOnlySubReleaseWhenParentReleaseAlreadyExists_GoingMoreDeeply() throws UnableToCompleteActionException {
		final Release releaseR2 = rootRelease.getChild(1);

		final ReleaseCreateAction createAction = new ReleaseCreateAction("R2/It4/Week1");
		ActionTestUtils.execute(createAction, context);

		// Do not create a new release for 'R2', neither for 'It4' because they already exist.
		assertEquals(3, rootRelease.getChildren().size());
		assertEquals("R2", releaseR2.getDescription());
		assertEquals(1, releaseR2.getChildren().size());
		assertEquals("It4", releaseR2.getChild(0).getDescription());
		// Create a new release called 'Week1' inside 'It4'
		assertEquals(1, releaseR2.getChild(0).getChildren().size());
		assertEquals("Week1", releaseR2.getChild(0).getChild(0).getDescription());
	}

	@Test
	public void shouldCreateAllNonExistentSubReleasesWhenParentReleaseAlreadyExists() throws UnableToCompleteActionException {
		final Release releaseR2 = rootRelease.getChild(1);

		final ReleaseCreateAction createAction = new ReleaseCreateAction("R2/It5/Week1");
		ActionTestUtils.execute(createAction, context);

		// Do not create a new release for 'R2', because it already exists.
		assertEquals(3, rootRelease.getChildren().size());
		assertEquals("R2", releaseR2.getDescription());
		// Create new releases: 'It5' inside 'R2' and 'Week1' inside 'It5'
		assertEquals(2, releaseR2.getChildren().size());
		assertEquals("It5", releaseR2.getChild(1).getDescription());
		assertEquals(1, releaseR2.getChild(1).getChildren().size());
		assertEquals("Week1", releaseR2.getChild(1).getChild(0).getDescription());
	}

	@Test
	public void entireReleaseHierarchyShouldBeInProjectContext() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("R4/It1");
		ActionTestUtils.execute(createAction, context);

		assertNotNull(context.findRelease("R4"));
		assertNotNull(context.findRelease("R4/It1"));
		assertEquals("It1", context.findRelease("R4/It1").getDescription());
	}

	@Test
	public void rollbackShouldRemoveReleasePreviouslyCreated() throws UnableToCompleteActionException {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("R4");

		final ModelAction rollback = ActionTestUtils.execute(createAction, context);
		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());

		ActionTestUtils.execute(rollback, context);
		assertEquals(3, rootRelease.getChildren().size());
		try {
			context.findRelease("R4");
			fail("A ReleaseNotFoundException should have been thrown.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

	@Test
	public void rollbackShouldRemoveSubReleasePreviouslyCreated() throws UnableToCompleteActionException {
		final ReleaseCreateAction createAction = new ReleaseCreateAction("R4/It1");

		final ModelAction rollback = ActionTestUtils.execute(createAction, context);
		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());

		ActionTestUtils.execute(rollback, context);
		assertEquals(3, rootRelease.getChildren().size());
		try {
			context.findRelease("R4/It1");
			fail("A ReleaseNotFoundException should have been thrown.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

	@Test
	public void shouldHandleRemovalCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ReleaseCreateAction("R4/It1"), context, Mockito.mock(ActionContext.class));

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
		assertNotNull(context.findRelease("R4"));
		assertNotNull(context.findRelease("R4/It1"));

		for (int i = 0; i < 20; i++) {
			// Undo
			actionExecutionManager.undoUserAction(context, Mockito.mock(ActionContext.class));

			assertEquals(3, rootRelease.getChildren().size());
			try {
				context.findRelease("R4");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}
			try {
				context.findRelease("R4/It1");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}

			// Redo
			actionExecutionManager.redoUserAction(context, Mockito.mock(ActionContext.class));

			assertEquals(4, rootRelease.getChildren().size());
			assertEquals("R4", rootRelease.getChild(3).getDescription());
			assertEquals(1, rootRelease.getChild(3).getChildren().size());
			assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
			assertNotNull(context.findRelease("R4"));
			assertNotNull(context.findRelease("R4/It1"));
		}
	}

	@Test
	public void shouldHandleRemovalCorrectlyAfterMultipleUndosAndRedos2() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final Release releaseR2 = rootRelease.getChild(1);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ReleaseCreateAction("R2/It5/Week1"), context, Mockito.mock(ActionContext.class));

		// Do not create a new release for 'R2', because it already exists.
		assertEquals(3, rootRelease.getChildren().size());
		assertEquals("R2", releaseR2.getDescription());
		// Create new releases: 'It5' inside 'R2' and 'Week1' inside 'It5'
		assertEquals(2, releaseR2.getChildren().size());
		assertEquals("It5", releaseR2.getChild(1).getDescription());
		assertEquals(1, releaseR2.getChild(1).getChildren().size());
		assertEquals("Week1", releaseR2.getChild(1).getChild(0).getDescription());

		assertNotNull(context.findRelease("R2/It5"));
		assertNotNull(context.findRelease("R2/It5/Week1"));

		for (int i = 0; i < 20; i++) {
			// Undo
			actionExecutionManager.undoUserAction(context, Mockito.mock(ActionContext.class));

			assertEquals(3, rootRelease.getChildren().size());
			assertEquals("R2", releaseR2.getDescription());

			assertEquals(1, releaseR2.getChildren().size());
			try {
				context.findRelease("R2/It5");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}
			try {
				context.findRelease("R2/It5/Week1");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}

			// Redo
			actionExecutionManager.redoUserAction(context, Mockito.mock(ActionContext.class));

			assertEquals(3, rootRelease.getChildren().size());
			assertEquals("R2", releaseR2.getDescription());

			assertEquals(2, releaseR2.getChildren().size());
			assertEquals("It5", releaseR2.getChild(1).getDescription());
			assertEquals(1, releaseR2.getChild(1).getChildren().size());
			assertEquals("Week1", releaseR2.getChild(1).getChild(0).getDescription());

			assertNotNull(context.findRelease("R2/It5"));
			assertNotNull(context.findRelease("R2/It5/Week1"));
		}
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseCreateActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseCreateAction.class;
	}

	@Override
	public void shouldSetReferenceUUIDBeforeExecution() throws Exception {
		final ReleaseCreateAction action = (ReleaseCreateAction) getNewInstance();
		assertNotNull(action.getNewReleaseId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ReleaseCreateAction("");
	}
}
