package br.com.oncast.ontrack.shared.model.progress;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class ProgressInferenceEngineFlow1Test {

	private final String FILE_NAME_PREFIX = "Flow1";
	private Scope rootScope = null;
	private ProjectContext projectContext;

	@Before
	public void setUp() {
		rootScope = ProgressInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
		projectContext = new ProjectContext(new Project(rootScope, new Release("proj")));
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyEffort();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
		shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope();
	}

	@Test
	public void testCaseStep04() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
		shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope();
		shouldApplyEffortInferenceAfterRemovingTwoScopes();
	}

	@Test
	public void testCaseStep05() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
		shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope();
		shouldApplyEffortInferenceAfterRemovingTwoScopes();
		shouldApplyEffortInferenceAfterCreatingSubScopeWithDeclaredEffortAndConcluded();
	}

	@Test
	public void testCaseStep06() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
		shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope();
		shouldApplyEffortInferenceAfterRemovingTwoScopes();
		shouldApplyEffortInferenceAfterCreatingSubScopeWithDeclaredEffortAndConcluded();
		shouldCorrectProgressInferenceAfterUpdatingNodeProgressToConcluded();
	}

	@Test
	public void testCaseStep07() throws UnableToCompleteActionException {
		shouldApplyEffort();
		shouldApplyInferenceWhenDeclaringLeafAsDone();
		shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope();
		shouldApplyEffortInferenceAfterRemovingTwoScopes();
		shouldApplyEffortInferenceAfterCreatingSubScopeWithDeclaredEffortAndConcluded();
		shouldCorrectProgressInferenceAfterUpdatingNodeProgressToConcluded();
		shouldCorrectEffortAndProgressInferenceAfterRemovingSubScopeWithDeclaredEffortAndConcluded();
	}

	private void shouldApplyEffort() {
		rootScope.getEffort().setDeclared(20);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 1), rootScope);
	}

	private void shouldApplyInferenceWhenDeclaringLeafAsDone() {
		final Scope parent = rootScope.getChild(3);
		final Scope scope = parent.getChild(0);

		scope.getEffort().setDeclared(5);
		scope.getProgress().setDescription("DONE");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 2), rootScope);
	}

	private void shouldApplyInferenceRemovingAccomplishedEffortFromParentWhenRemovingScope() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(3);
		final Scope scope = parent.getChild(0);

		new ScopeRemoveAction(scope.getId()).execute(getProjectContext());
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 3), rootScope);
	}

	private void shouldApplyEffortInferenceAfterRemovingTwoScopes() throws UnableToCompleteActionException {
		final Scope parent = rootScope;

		new ScopeRemoveAction(rootScope.getChild(3).getId()).execute(getProjectContext());
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		new ScopeRemoveAction(rootScope.getChild(2).getId()).execute(getProjectContext());
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 4), rootScope);
	}

	private void shouldApplyEffortInferenceAfterCreatingSubScopeWithDeclaredEffortAndConcluded() throws UnableToCompleteActionException {
		final Scope parent = rootScope;
		final Scope scope = parent.getChild(1);

		new ScopeInsertChildAction(scope.getId(), "b1 #5 %DONE").execute(getProjectContext());
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 5), rootScope);
	}

	private void shouldCorrectProgressInferenceAfterUpdatingNodeProgressToConcluded() throws UnableToCompleteActionException {
		final Scope parent = rootScope;
		final Scope scope = parent.getChild(0);

		scope.getProgress().setDescription("DONE");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), rootScope);
	}

	private void shouldCorrectEffortAndProgressInferenceAfterRemovingSubScopeWithDeclaredEffortAndConcluded() throws UnableToCompleteActionException {
		final Scope parent = rootScope.getChild(1);
		final Scope scope = parent.getChild(0);

		new ScopeRemoveAction(scope.getId()).execute(getProjectContext());
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(parent);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 7), rootScope);
	}

	private ProjectContext getProjectContext() {
		return projectContext;
	}
}
