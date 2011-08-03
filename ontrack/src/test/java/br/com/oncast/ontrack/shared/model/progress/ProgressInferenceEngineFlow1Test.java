package br.com.oncast.ontrack.shared.model.progress;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getOriginalScope;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;

public class ProgressInferenceEngineFlow1Test {

	private final String FILE_NAME_PREFIX = "Flow1";
	private Scope original = null;
	private final InferenceOverScopeEngine inferenceEngine = new ProgressInferenceEngine();

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyInferenceWhenDeclaringLeafAsDone();
	}

	private void shouldApplyInferenceWhenDeclaringLeafAsDone() {
		original.getEffort().setDeclared(1000);
		inferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 1));
	}
}
