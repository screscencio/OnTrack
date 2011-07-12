package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getOriginalScope;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class Flow1 {

	private final String FILE_NAME = "Flow1";
	final Scope original = getOriginalScope(FILE_NAME);

	@Test
	public void shouldApplyInferencesWhenEffortChanges() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortIsDeclared();
	}

	private void shouldApplyInferenceTopDownWhenRootIsModified() {
		original.getEffort().setDeclared(1000);
		EffortInferenceEngine.process(original);

		assertTrue(original.deepEquals(getModifiedScope(FILE_NAME, 1)));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded() {
		final Scope newScope = new Scope("Cancelar pedido");
		original.getChild(1).add(newScope);
		EffortInferenceEngine.process(original);

		assertEquals(newScope.getEffort().getInfered(), 62.5, 0.1);
		assertTrue(original.deepEquals(getModifiedScope(FILE_NAME, 2)));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentEffortIsDeclared() {
		final Scope scopeWithChangedEffort = original.getChild(1);
		scopeWithChangedEffort.getEffort().setDeclared(350);
		EffortInferenceEngine.process(scopeWithChangedEffort);

		for (final Scope child : scopeWithChangedEffort.getChildren()) {
			assertEquals(child.getEffort().getInfered(), 87.5, 0.1);
		}
		assertTrue(original.deepEquals(getModifiedScope(FILE_NAME, 3)));
	}
}
