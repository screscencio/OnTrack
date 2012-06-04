package br.com.oncast.ontrack.shared.model.value;

import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getOriginalScope;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ValueInferenceEngineFlow6Test {

	private final String FILE_NAME_PREFIX = "Flow6";
	private Scope original = null;
	private ProjectContext projectContext;
	private final Stack<ModelAction> rollbackActions = new Stack<ModelAction>();
	private final Stack<ModelAction> redoActions = new Stack<ModelAction>();

	@Before
	public void setUp() throws UnableToCompleteActionException, ScopeNotFoundException {
		original = getOriginalScope(FILE_NAME_PREFIX);
		projectContext = ProjectTestUtils.createProjectContext(original, ReleaseFactoryTestUtil.create("proj"));
		DeepEqualityTestUtils.setRequiredFloatingPointPrecision(0.1);

		executeAction(new ScopeDeclareValueAction(original.getChild(0).getId(), true, 30));
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldDistributeTopDownValueWhenAddingAChildToAnAlreadyDeclaredParent();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldDistributeTopDownValueWhenAddingAChildToAnAlreadyDeclaredParent();
		shouldRemoveChildAfterUndo();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException, ScopeNotFoundException {
		shouldDistributeTopDownValueWhenAddingAChildToAnAlreadyDeclaredParent();
		shouldRemoveChildAfterUndo();
		shouldRedistributeTopDownValueAfterRedo();
	}

	private void shouldDistributeTopDownValueWhenAddingAChildToAnAlreadyDeclaredParent() throws UnableToCompleteActionException, ScopeNotFoundException {
		rollbackActions.push(executeAction(new ScopeInsertChildAction(original.getChild(0).getId(), "a1")));
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 2), original);

		rollbackActions.push(executeAction(new ScopeInsertSiblingDownAction(original.getChild(0).getChild(0).getId(), "a2")));
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRemoveChildAfterUndo() throws UnableToCompleteActionException, ScopeNotFoundException {
		redoActions.push(executeAction(rollbackActions.pop()));
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 2), original);

		redoActions.push(executeAction(rollbackActions.pop()));
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeTopDownValueAfterRedo() throws UnableToCompleteActionException, ScopeNotFoundException {
		executeAction(redoActions.pop());
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 2), original);

		executeAction(redoActions.pop());
		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private ModelAction executeAction(final ModelAction action) throws UnableToCompleteActionException, ScopeNotFoundException {
		final Scope valueInferenceBaseScopeForTestingPurposes = ActionExecuterTestUtils.getInferenceBaseScopeForTestingPurposes(projectContext, action);
		final ModelAction rollbackAction = action.execute(projectContext, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(valueInferenceBaseScopeForTestingPurposes);
		return rollbackAction;
	}
}
