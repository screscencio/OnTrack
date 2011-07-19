package br.com.oncast.ontrack.shared.model.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.STATUS;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class ScopeProgressActionTest {

	private Scope scope;
	private ProjectContext context;

	@Before
	public void setUp() {
		scope = new Scope("scope description");
		context = new ProjectContext(new Project(scope, new Release("")));
	}

	@Test
	public void notStartedShouldBeTheDefaultProgress() throws UnableToCompleteActionException {
		assertThatProgressIs(STATUS.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressToAScope() throws UnableToCompleteActionException {
		new ScopeProgressAction(scope.getId(), "Not started").execute(context);

		assertEquals("Not started", scope.getProgress().getDescription());
	}

	@Test
	public void shouldResetProgressOfScope() throws UnableToCompleteActionException {
		final Progress progress = new Progress();
		progress.setDescription("Under work");
		scope.setProgress(progress);

		new ScopeProgressAction(scope.getId(), "").execute(context);

		assertEquals("", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWorkIfDescriptionDoesntFitInOthersStatuses() throws UnableToCompleteActionException {
		new ScopeProgressAction(scope.getId(), "Anything").execute(context);

		assertEquals("Anything", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWork() throws UnableToCompleteActionException {
		new ScopeProgressAction(scope.getId(), "Under work").execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToDone() throws UnableToCompleteActionException {
		new ScopeProgressAction(scope.getId(), "Done").execute(context);

		assertEquals("Done", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.DONE);
	}

	@Test
	public void shouldSetProgressStatusOfScopeToNotStartedConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableNotStartedDescriptions = { "NotStarted", "Not Started", "Not started", "not started", "not_started", "Not_Started", "NS",
				"ns", "N", "n" };
		for (final String notStartedDespription : acceptableNotStartedDescriptions) {
			new ScopeProgressAction(scope.getId(), notStartedDespription).execute(context);
			assertThatProgressIs(STATUS.NOT_STARTED);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToUnderWorkConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableUnderWorkDescriptions = { "Under work", "Under_work", "under work", "design", "coding", "testing", "acceptance", "anything" };
		for (final String underWorkDespription : acceptableUnderWorkDescriptions) {
			new ScopeProgressAction(scope.getId(), underWorkDespription).execute(context);
			assertThatProgressIs(STATUS.UNDER_WORK);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToDoneConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableDoneDescriptions = { "Done", "DONE", "done", "DN", "Dn", "dn", "D", "d" };
		for (final String doneDespription : acceptableDoneDescriptions) {
			new ScopeProgressAction(scope.getId(), doneDespription).execute(context);
			assertThatProgressIs(STATUS.DONE);
		}
	}

	@Test
	public void shouldRevertChangesAfterARollback() throws UnableToCompleteActionException {
		scope.getProgress().setDescription("Done");
		assertEquals("Done", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.DONE);

		final ScopeProgressAction progressAction = new ScopeProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.UNDER_WORK);

		rollbackAction.execute(context);

		assertEquals("Done", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.DONE);
	}

	@Test
	public void shouldRevertChangesAfterARollback2() throws UnableToCompleteActionException {
		assertThatProgressIs(STATUS.NOT_STARTED);

		final ScopeProgressAction progressAction = new ScopeProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(STATUS.UNDER_WORK);

		rollbackAction.execute(context);

		assertThatProgressIs(STATUS.NOT_STARTED);
	}

	private void assertThatProgressIs(final Progress.STATUS status) {
		assertEquals(status, scope.getProgress().getStatus());
	}
}