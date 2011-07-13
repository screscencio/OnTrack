package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.TestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.TestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class Flow1Test {

	private final String FILE_NAME = "Flow1";
	private final Scope original = getOriginalScope(FILE_NAME);

	@Test
	public void shouldApplyInferencesWhenEffortChanges() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
	}

	private void shouldApplyInferenceTopDownWhenRootIsModified() {
		original.getEffort().setDeclared(1000);
		EffortInferenceEngine.process(original);

		assertDeepEquals(original, getModifiedScope(FILE_NAME, 1));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded() {
		final Scope newScope = new Scope("Cancelar pedido");
		original.getChild(1).add(newScope);
		EffortInferenceEngine.process(newScope.getParent());

		assertDeepEquals(original, getModifiedScope(FILE_NAME, 2));
	}

	private void shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared() {
		final Scope scopeWithChangedEffort = original.getChild(1);
		scopeWithChangedEffort.getEffort().setDeclared(350);
		EffortInferenceEngine.process(scopeWithChangedEffort.getParent());

		for (final Scope child : scopeWithChangedEffort.getChildren()) {
			assertEquals(87.5, child.getEffort().getInfered(), 0.09);
		}

		assertDeepEquals(original, getModifiedScope(FILE_NAME, 3));
	}

	private void shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared() {
		final Scope scopeWithChangedEffort = original.getChild(1).getChild(0);
		scopeWithChangedEffort.getEffort().setDeclared(150);
		EffortInferenceEngine.process(scopeWithChangedEffort.getParent());

		assertEquals(66.6, original.getChild(1).getChild(1).getEffort().getInfered(), 0.09);
		assertEquals(66.6, original.getChild(1).getChild(2).getEffort().getInfered(), 0.09);
		assertEquals(66.6, original.getChild(1).getChild(3).getEffort().getInfered(), 0.09);

		assertDeepEquals(original, getModifiedScope(FILE_NAME, 4));
	}

	private void shouldRedistributeEffortBetweenSiblingWhenInferedChanges() {
		final Scope parentScopeWithChildrenModification = original.getChild(1);

		parentScopeWithChildrenModification.getChild(1).getEffort().setDeclared(150);
		EffortInferenceEngine.process(parentScopeWithChildrenModification);

		parentScopeWithChildrenModification.getChild(2).getEffort().setDeclared(150);
		EffortInferenceEngine.process(parentScopeWithChildrenModification);

		parentScopeWithChildrenModification.getChild(3).getEffort().setDeclared(150);
		EffortInferenceEngine.process(parentScopeWithChildrenModification);

		assertEquals(600, parentScopeWithChildrenModification.getEffort().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(0).getEffort().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(2).getEffort().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(3).getEffort().getInfered(), 0.09);

		assertDeepEquals(original, getModifiedScope(FILE_NAME, 5));
	}

}
