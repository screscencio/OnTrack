package br.com.oncast.ontrack.shared.model.actions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class CreateReleaseActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeMock.getScope();
		rootRelease = ReleaseMock.getRelease();
		context = new ProjectContext(new Project(rootScope, rootRelease));

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test
	public void shouldCreateNewReleaseInsideProjectReleaseWhenNoRelativeReleaseIsProvided() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("New release");
		createAction.execute(context);

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("New release", rootRelease.getChild(3).getDescription());
	}

	@Test
	public void shouldCreateNewReleaseInsideSpecifiedReleaseWhenItIsProvided() throws UnableToCompleteActionException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault(rootRelease.getChild(0).getId(), "New release");
		createAction.execute(context);

		assertEquals(4, rootRelease.getChild(0).getChildren().size());
		assertEquals("New release", rootRelease.getChild(0).getChild(3).getDescription());
	}

	@Test
	public void shouldCreateEntireReleaseHierarchy() throws UnableToCompleteActionException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("R4/It1");
		createAction.execute(context);

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
	}

	@Test
	public void entireReleaseHierarchyShouldBeInProjectContext() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("R4/It1");
		createAction.execute(context);

		assertNotNull(context.loadRelease("R4"));
		assertNotNull(context.loadRelease("R4/It1"));
		assertEquals("It1", context.loadRelease("R4/It1").getDescription());
	}

	@Test
	public void rollbackShouldRemoveReleasePreviouslyCreated() throws UnableToCompleteActionException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("R4");

		final ModelAction rollback = createAction.execute(context);
		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());

		rollback.execute(context);
		assertEquals(3, rootRelease.getChildren().size());
		try {
			context.loadRelease("R4");
			fail("A ReleaseNotFoundException should have been thrown.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

	@Test
	public void rollbackShouldRemoveSubReleasePreviouslyCreated() throws UnableToCompleteActionException {
		final ReleaseCreateActionDefault createAction = new ReleaseCreateActionDefault("R4/It1");

		final ModelAction rollback = createAction.execute(context);
		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());

		rollback.execute(context);
		assertEquals(3, rootRelease.getChildren().size());
		try {
			context.loadRelease("R4/It1");
			fail("A ReleaseNotFoundException should have been thrown.");
		}
		catch (final ReleaseNotFoundException e) {}
	}

	@Test
	public void shouldHandleRemovalCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException, ReleaseNotFoundException {
		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ReleaseCreateActionDefault("R4/It1"), context);

		assertEquals(4, rootRelease.getChildren().size());
		assertEquals("R4", rootRelease.getChild(3).getDescription());
		assertEquals(1, rootRelease.getChild(3).getChildren().size());
		assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
		assertNotNull(context.loadRelease("R4"));
		assertNotNull(context.loadRelease("R4/It1"));

		for (int i = 0; i < 20; i++) {
			// Undo
			actionExecutionManager.undoUserAction(context);

			assertEquals(3, rootRelease.getChildren().size());
			try {
				context.loadRelease("R4");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}
			try {
				context.loadRelease("R4/It1");
				fail("A ReleaseNotFoundException should have been thrown.");
			}
			catch (final ReleaseNotFoundException e) {}

			// Redo
			actionExecutionManager.redoUserAction(context);

			assertEquals(4, rootRelease.getChildren().size());
			assertEquals("R4", rootRelease.getChild(3).getDescription());
			assertEquals(1, rootRelease.getChild(3).getChildren().size());
			assertEquals("It1", rootRelease.getChild(3).getChild(0).getDescription());
			assertNotNull(context.loadRelease("R4"));
			assertNotNull(context.loadRelease("R4/It1"));
		}
	}
}
