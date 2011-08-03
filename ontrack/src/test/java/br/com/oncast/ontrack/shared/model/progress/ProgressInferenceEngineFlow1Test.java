package br.com.oncast.ontrack.shared.model.progress;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class ProgressInferenceEngineFlow1Test {

	private final String FILE_NAME_PREFIX = "Flow1";
	private Scope rootScope = null;

	@Before
	public void setUp() {
		rootScope = ProgressInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyEffort();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyEffort();
	}

	private void shouldApplyEffort() {
		rootScope.getEffort().setDeclared(20);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 1), rootScope);
	}

}
