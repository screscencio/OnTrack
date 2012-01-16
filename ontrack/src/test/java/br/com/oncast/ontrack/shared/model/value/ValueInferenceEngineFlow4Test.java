package br.com.oncast.ontrack.shared.model.value;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ValueInferenceEngineFlow4Test {

	private final String FILE_NAME_PREFIX = "Flow4";
	private Scope rootScope = null;
	private ProjectContext projectContext;
	private final Stack<ModelAction> rollbackActions = new Stack<ModelAction>();

	@Before
	public void setUp() {
		rootScope = ValueInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
		projectContext = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create("proj"));
		DeepEqualityTestUtils.setRequiredFloatingPointPrecision(0.1);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep04() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
	}

	@Test
	public void testCaseStep05() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
		shouldApplyValueInA3AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep06() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
		shouldApplyValueInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree2();
	}

	@Test
	public void testCaseStep07() throws UnableToCompleteActionException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
		shouldApplyValueInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyValueInA22AndInferenceOverTheRestOfTheTree();
	}

	@Test
	public void testCaseStep08() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
		shouldApplyValueInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyValueInA22AndInferenceOverTheRestOfTheTree();
		shouldReturnToInitialStateAfterRollbackingAllActions();
	}

	@Test
	public void testCaseStep09() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldApplyValueAtProjectRoot();
		shouldApplyValueInA1AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree();
		shouldReApplyValueInferenceAfterUndo();
		shouldApplyValueInA3AndInferenceOverTheRestOfTheTree();
		shouldApplyValueInA21AndInferenceOverTheRestOfTheTree2();
		shouldApplyValueInA22AndInferenceOverTheRestOfTheTree();
		shouldReturnToInitialStateAfterRollbackingAllActions();
	}

	private void shouldReturnToInitialStateAfterRollbackingAllActions() throws UnableToCompleteActionException, ScopeNotFoundException {
		while (!rollbackActions.isEmpty()) {
			final ModelAction rollbackAction = rollbackActions.pop();
			rollbackAction.execute(projectContext);
			ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(ActionExecuterTestUtils.getInferenceBaseScopeForTestingPurposes(
					projectContext, rollbackAction));
		}
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX), rootScope);
	}

	private ModelAction executeAction(final Scope scope, final ModelAction action) throws UnableToCompleteActionException {
		final ModelAction rollbackAction = action.execute(projectContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(scope);
		return rollbackAction;
	}

	private void shouldApplyValueAtProjectRoot() throws UnableToCompleteActionException {
		rollbackActions.push(executeAction(rootScope, new ScopeDeclareValueAction(rootScope.getId(), true, 1000)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 1), rootScope);
	}

	private void shouldApplyValueInA1AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0);
		final Scope scope = parent.getChild(0);

		rollbackActions.push(executeAction(parent, new ScopeDeclareValueAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 2), rootScope);
	}

	private void shouldApplyValueInA21AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0).getChild(1);
		final Scope scope = parent.getChild(0);

		rollbackActions.push(executeAction(parent, new ScopeDeclareValueAction(scope.getId(), true, 20)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 3), rootScope);
	}

	private void shouldReApplyValueInferenceAfterUndo() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0).getChild(1);

		executeAction(parent, rollbackActions.pop());
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 4), rootScope);
	}

	private void shouldApplyValueInA3AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0);
		final Scope scope = parent.getChild(2);

		rollbackActions.push(executeAction(parent, new ScopeDeclareValueAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 5), rootScope);
	}

	private void shouldApplyValueInA21AndInferenceOverTheRestOfTheTree2() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0).getChild(1);
		final Scope scope = parent.getChild(0);

		rollbackActions.push(executeAction(parent, new ScopeDeclareValueAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), rootScope);
	}

	private void shouldApplyValueInA22AndInferenceOverTheRestOfTheTree() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(0).getChild(1);
		final Scope scope = parent.getChild(1);

		rollbackActions.push(executeAction(parent, new ScopeDeclareValueAction(scope.getId(), true, 10)));
		DeepEqualityTestUtils.assertObjectEquality(ValueInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 7), rootScope);
	}
}
