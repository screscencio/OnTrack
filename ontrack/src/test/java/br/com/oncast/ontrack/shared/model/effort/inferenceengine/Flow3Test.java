package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.utils.TestUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.TestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class Flow3Test {

	private final String FILE_NAME_PREFIX = "Flow3";
	private final Scope original = getOriginalScope(FILE_NAME_PREFIX);

	@Test
	public void shouldApplyInferencesWhenEffortChanges() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
		shouldRedistributeInferencesWhenChildrenReceiveEffortDeclarations();
		shouldRedistributeInferencesWhenSiblingReceiveEffortDeclarations();
	}

	private void shouldApplyInferenceTopDownThroughChildren() {
		original.getChild(0).getEffort().setDeclared(12);
		EffortInferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 1));
	}

	private void shouldRedistributeInferencesWhenChildrenReceiveEffortDeclarations() {
		final Scope scope = original.getChild(0).getChild(0);
		scope.getChild(0).getEffort().setDeclared(8);
		EffortInferenceEngine.process(scope);
		scope.getChild(1).getEffort().setDeclared(8);
		EffortInferenceEngine.process(scope);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 2));
	}

	private void shouldRedistributeInferencesWhenSiblingReceiveEffortDeclarations() {
		final Scope scope = original.getChild(0);
		scope.getChild(1).getEffort().setDeclared(20);
		EffortInferenceEngine.process(scope);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 3));
	}
}
