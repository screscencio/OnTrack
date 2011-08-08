package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getOriginalScope;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class EffortInferenceEngineFlow5Test {

	private final String FILE_NAME_PREFIX = "Flow5";
	private Scope original = null;
	private final EffortInferenceEngine effortInferenceEngine = new EffortInferenceEngine();

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyInferenceBottomUpThroughAncestors();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyInferenceBottomUpThroughAncestors();
		shouldRedistributeEffortThroughSiblings();
	}

	private void shouldApplyInferenceBottomUpThroughAncestors() {
		original.getChild(0).getChild(1).getEffort().setDeclared(30);
		effortInferenceEngine.process(original.getChild(0));

		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeEffortThroughSiblings() {
		original.getChild(0).getEffort().setDeclared(50);
		effortInferenceEngine.process(original);

		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

}
