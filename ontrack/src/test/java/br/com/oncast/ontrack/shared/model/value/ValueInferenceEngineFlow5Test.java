package br.com.oncast.ontrack.shared.model.value;

import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getOriginalScope;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ValueInferenceEngineFlow5Test {

	private final String FILE_NAME_PREFIX = "Flow5";
	private Scope original = null;
	private final ValueInferenceEngine valueInferenceEngine = new ValueInferenceEngine();

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
		shouldRedistributeValueThroughSiblings();
	}

	private void shouldApplyInferenceBottomUpThroughAncestors() {
		original.getChild(0).getChild(1).getValue().setDeclared(30);
		valueInferenceEngine.process(original.getChild(0), UserTestUtils.getAdmin(), new Date());

		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeValueThroughSiblings() {
		original.getChild(0).getValue().setDeclared(50);
		valueInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

}
