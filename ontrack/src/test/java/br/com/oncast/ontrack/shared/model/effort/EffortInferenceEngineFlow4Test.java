package br.com.oncast.ontrack.shared.model.effort;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EffortInferenceEngineFlow4Test {

	private final String FILE_NAME_PREFIX = "Flow4";
	private Scope rootScope = null;
	private ProjectContext projectContext;
	private final Stack<ModelAction> rollbackActions = new Stack<ModelAction>();

	@Before
	public void setUp() {
		rootScope = EffortInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
		projectContext = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease("proj"));
		DeepEqualityTestUtils.setRequiredFloatingPointPrecision(0.1);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep04() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
	}

	@Test
	public void testCaseStep05() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
		shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep06() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
		shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree2();
	}

	@Test
	public void testCaseStep07() throws UnableToCompleteActionException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
		shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyEffortInA22AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep08() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
		shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyEffortInA22AndInferenceOverTheRestOfTheTree();
		shouldReturnToInitialStateAfterRollbackingAllActions();
	}

	@Test
	public void testCaseStep09() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldApplyEffortAtProjectRoot();
		shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyEffortInferenceAfterUndo();
		shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyEffortInA22AndInferenceOverTheRestOfTheTree();
		shouldReturnToInitialStateAfterRollbackingAllActions();
	}

	private void shouldReturnToInitialStateAfterRollbackingAllActions() throws UnableToCompleteActionException, ScopeNotFoundException {
		while (!rollbackActions.isEmpty()) {
			final ModelAction rollbackAction = rollbackActions.pop();
			rollbackAction.execute(projectContext, Mockito.mock(ActionContext.class));
			ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(ActionExecuterTestUtils.getInferenceBaseScopeForTestingPurposes(projectContext, rollbackAction));
		}
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX), rootScope);
	}

	private ModelAction executeAction(final ModelAction action) throws UnableToCompleteActionException {
		final ModelAction rollbackAction = action.execute(projectContext, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(ActionHelper.findScope(action.getReferenceId(), projectContext, action));
		return rollbackAction;
	}

	private void shouldApplyEffortAtProjectRoot() throws UnableToCompleteActionException {
		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(rootScope.getId(), true, 1000)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 1), rootScope);
	}

	private void shouldApplyEffortInA1AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0).getChild(0);

		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 2), rootScope);
	}

	private void shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0).getChild(1).getChild(0);

		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(scope.getId(), true, 20)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 3), rootScope);
	}

	private void shouldReApplyEffortInferenceAfterUndo() throws UnableToCompleteActionException {
		executeAction(rollbackActions.pop());
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 4), rootScope);
	}

	private void shouldApplyEffortInA3AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0).getChild(2);

		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 5), rootScope);
	}

	private void shouldApplyEffortInA21AndInferenceOverTheRestOfTheTree2() throws UnableToCompleteActionException {
		final Scope scope = rootScope.getChild(0).getChild(1).getChild(0);

		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), rootScope);
	}

	private void shouldApplyEffortInA22AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0).getChild(1);
		final Scope scope = parent.getChild(1);

		rollbackActions.push(executeAction(new ScopeDeclareEffortAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(EffortInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 7), rootScope);
	}
}
