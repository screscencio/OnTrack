package br.com.oncast.ontrack.shared.model.action;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceTestUtils;
import br.com.oncast.ontrack.shared.model.progress.ProgressTestUtils;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ScopeInsertChildAction_ProgressUndoAndRedoTest {

	private final String FILE_NAME_PREFIX = "Flow1";

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeInsertionWithDeclaredProgressInScopeWithoutProgress()
			throws UnableToCompleteActionException {
		final Scope currentScope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 4);

		final Scope parent = currentScope;
		final Scope scope = parent.getChild(1);
		final ProjectContext context = ProjectTestUtils.createProjectContext(currentScope, ReleaseFactoryTestUtil.create("r"));

		ModelAction action = new ScopeInsertChildAction(scope.getId(), "b1 #5 %DONE");
		ModelAction rollbackAction = executeAction(parent, action, context);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 5), currentScope);

		for (int i = 0; i < 10; i++) {
			action = executeAction(parent, rollbackAction, context);
			DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 4), currentScope);
			rollbackAction = executeAction(parent, action, context);
			DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 5), currentScope);
		}
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeInsertionWithDeclaredProgressInScopeWithProgress()
			throws UnableToCompleteActionException {
		final Scope currentScope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope parent = currentScope.getChild(1);
		final Scope scope = parent.getChild(0);
		final ProjectContext context = ProjectTestUtils.createProjectContext(currentScope, ReleaseFactoryTestUtil.create("r"));

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);

		ModelAction action = new ScopeInsertChildAction(scope.getId(), "b11 %DONE");
		ModelAction rollbackAction = executeAction(parent, action, context);

		DeepEqualityTestUtils.assertObjectEquality(
				getModifiedScopeForTestThatShouldReturnToSameStateAfterMultipleUndosAndRedosInScopeInsertionWithDeclaredProgressInScopeWithProgress(),
				currentScope);

		for (int i = 0; i < 10; i++) {
			action = executeAction(parent, rollbackAction, context);
			DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);
			rollbackAction = executeAction(parent, action, context);
			DeepEqualityTestUtils.assertObjectEquality(
					getModifiedScopeForTestThatShouldReturnToSameStateAfterMultipleUndosAndRedosInScopeInsertionWithDeclaredProgressInScopeWithProgress(),
					currentScope);
		}
	}

	private Object getModifiedScopeForTestThatShouldReturnToSameStateAfterMultipleUndosAndRedosInScopeInsertionWithDeclaredProgressInScopeWithProgress() {
		final Scope scope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope child = scope.getChild(1).getChild(0);
		child.getProgress().setDescription("DONE");
		ProgressTestUtils.setProgressState(child, ProgressState.DONE);

		final Scope grandChild = new Scope("b11");
		grandChild.getProgress().setDescription("DONE");
		child.add(grandChild);

		return scope;
	}

	private ModelAction executeAction(final Scope scope, final ModelAction action, final ProjectContext context) throws UnableToCompleteActionException {
		final ModelAction rollbackAction = action.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(scope);
		return rollbackAction;
	}
}
