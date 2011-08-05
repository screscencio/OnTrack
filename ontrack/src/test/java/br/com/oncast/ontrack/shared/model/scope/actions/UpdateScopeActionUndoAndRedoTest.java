package br.com.oncast.ontrack.shared.model.scope.actions;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceTestUtils;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class UpdateScopeActionUndoAndRedoTest {

	private final String FILE_NAME_PREFIX = "Flow1";

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgress1()
			throws UnableToCompleteActionException {
		runWith("b1 bla " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "DONE");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgress2()
			throws UnableToCompleteActionException {
		runWith("b1 bla " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "test");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithoutProgress()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgressAndEffort()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "7 " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "uw");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithEffort()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "7");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithRelease()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "r1");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithReleaseAndEffortAndProgress()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "reelease " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "0 "
				+ StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "NOT_STARTED");
	}

	private void runWith(final String updatePattern) throws UnableToCompleteActionException {
		final Scope currentScope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope parent = currentScope.getChild(1);
		final Scope scope = parent.getChild(0);
		final ProjectContext context = new ProjectContext(new Project(currentScope, new Release("r")));

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);

		ModelAction action = new ScopeUpdateAction(scope.getId(), updatePattern);
		ModelAction rollbackAction = executeAction(parent, action, context);

		DeepEqualityTestUtils.assertObjectEquality(
				getModifiedScope(updatePattern),
				currentScope);

		for (int i = 0; i < 10; i++) {
			action = executeAction(parent, rollbackAction, context);
			DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);
			rollbackAction = executeAction(parent, action, context);
			DeepEqualityTestUtils.assertObjectEquality(
					getModifiedScope(updatePattern),
					currentScope);
		}
	}

	private Object getModifiedScope(final String updatePattern)
			throws UnableToCompleteActionException {
		final Scope scope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope parent = scope.getChild(1);
		final Scope child = parent.getChild(0);
		executeAction(parent, new ScopeUpdateAction(child.getId(), updatePattern), new ProjectContext(new Project(scope, new Release(""))));

		return scope;
	}

	private ModelAction executeAction(final Scope scope, final ModelAction action, final ProjectContext context) throws UnableToCompleteActionException {
		final ModelAction rollbackAction = action.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(scope);
		return rollbackAction;
	}
}
