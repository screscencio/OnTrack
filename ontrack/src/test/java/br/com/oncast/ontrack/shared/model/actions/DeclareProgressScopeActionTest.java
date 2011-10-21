package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.TestReleaseFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class DeclareProgressScopeActionTest {

	private Scope scope;
	private ProjectContext context;

	@Before
	public void setUp() {
		scope = new Scope("scope description");
		context = new ProjectContext(new Project(scope, TestReleaseFactory.create("")));
	}

	@Test
	public void notStartedShouldBeTheDefaultProgress() throws UnableToCompleteActionException {
		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressToAScope() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Not started").execute(context);

		assertEquals(ProgressState.NOT_STARTED.getDescription(), scope.getProgress().getDescription());
	}

	@Test
	public void shouldResetProgressOfScope() throws UnableToCompleteActionException {
		scope.getProgress().setDescription("Under work");

		new ScopeDeclareProgressAction(scope.getId(), "").execute(context);

		assertEquals("", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWorkIfDescriptionDoesntFitInOthersStatuses() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Anything").execute(context);

		assertEquals("Anything", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWork() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Under work").execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToDone() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Done").execute(context);

		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);
	}

	@Test
	public void shouldSetProgressStatusOfScopeToNotStartedConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableNotStartedDescriptions = { "NotStarted", "Not Started", "Not started", "not started", "not_started", "Not_Started", "NS",
				"ns", "N", "n" };
		for (final String notStartedDespription : acceptableNotStartedDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), notStartedDespription).execute(context);
			assertThatProgressIs(ProgressState.NOT_STARTED);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToUnderWorkConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableUnderWorkDescriptions = { "Under work", "Under_work", "under work", "design", "coding", "testing", "acceptance", "anything" };
		for (final String underWorkDespription : acceptableUnderWorkDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), underWorkDespription).execute(context);
			assertThatProgressIs(ProgressState.UNDER_WORK);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToDoneConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableDoneDescriptions = { "Done", "DONE", "done", "DN", "Dn", "dn", "D", "d" };
		for (final String doneDespription : acceptableDoneDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), doneDespription).execute(context);
			assertThatProgressIs(ProgressState.DONE);
		}
	}

	@Test
	public void shouldRevertChangesAfterARollback() throws UnableToCompleteActionException {
		scope.getProgress().setDescription("Done");
		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);

		rollbackAction.execute(context);

		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);
	}

	@Test
	public void shouldRevertChangesAfterARollback2() throws UnableToCompleteActionException {
		assertThatProgressIs(ProgressState.NOT_STARTED);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);

		rollbackAction.execute(context);

		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	private void assertThatProgressIs(final Progress.ProgressState status) {
		assertEquals(status, scope.getProgress().getState());
	}
}
