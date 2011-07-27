package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.mmConverter.MindMapImporterUtils.getOriginalScope;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Flow2Test {

	private final String FILE_NAME_PREFIX = "Flow2";
	private Scope original = null;

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
	}

	@Test
	public void testCaseStep02() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
	}

	@Test
	public void testCaseStep03() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
		shouldRedistribuiteEffortWhenRootEffortIsChanged();
	}

	@Test
	public void testCaseStep04() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
		shouldRedistribuiteEffortWhenRootEffortIsChanged();
		shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort();
	}

	private void shouldInferBottomUpFromModifiedScopeAndTopDownFromIt() {
		original.getEffort().setDeclared(30);
		EffortInferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 1));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared() {
		final Scope a2 = original.getChild(0).getChild(1);
		a2.getEffort().setDeclared(10);
		EffortInferenceEngine.process(a2.getParent());

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 2));
	}

	private void shouldRedistribuiteEffortWhenRootEffortIsChanged() {
		original.getEffort().setDeclared(60);
		EffortInferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 3));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort() {
		final Scope a21 = original.getChild(0).getChild(1).getChild(0);
		a21.getEffort().setDeclared(7);
		EffortInferenceEngine.process(a21.getParent());

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 4));

	}

}
