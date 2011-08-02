package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getOriginalScope;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class InferenceEngineFlow3Test {

	private final String FILE_NAME_PREFIX = "Flow3";
	private Scope original = null;
	private final EffortInferenceEngine effortInferenceEngine = new EffortInferenceEngine();

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
		shouldRedistributeInferencesWhenChildrenReceiveEffortDeclarations();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
		shouldRedistributeInferencesWhenChildrenReceiveEffortDeclarations();
		shouldRedistributeInferencesWhenSiblingReceiveEffortDeclarations();
	}

	private void shouldApplyInferenceTopDownThroughChildren() {
		original.getChild(0).getEffort().setDeclared(12);
		effortInferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 1));
	}

	private void shouldRedistributeInferencesWhenChildrenReceiveEffortDeclarations() {
		final Scope scope = original.getChild(0).getChild(0);
		scope.getChild(0).getEffort().setDeclared(8);
		effortInferenceEngine.process(scope);
		scope.getChild(1).getEffort().setDeclared(8);
		effortInferenceEngine.process(scope);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 2));
	}

	private void shouldRedistributeInferencesWhenSiblingReceiveEffortDeclarations() {
		final Scope scope = original.getChild(0);
		scope.getChild(1).getEffort().setDeclared(20);
		effortInferenceEngine.process(scope);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 3));
	}
}
