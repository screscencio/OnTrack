package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.TestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.TestUtils.getOriginalScope;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Flow2Test {

	private final String FILE_NAME = "Flow2";
	private final Scope project = getOriginalScope(FILE_NAME);

	@Test
	public void shouldApplyInferencesWhenEffortChanges() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
		shouldRedistribuiteEffortWhenRootEffortIsChanged();
		shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort();
	}

	private void shouldInferBottomUpFromModifiedScopeAndTopDownFromIt() {
		project.getEffort().setDeclared(30);
		EffortInferenceEngine.process(project);

		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 1)));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared() {
		final Scope a2 = project.getChild(0).getChild(1);
		a2.getEffort().setDeclared(10);
		EffortInferenceEngine.process(a2.getParent());

		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 2)));
	}

	private void shouldRedistribuiteEffortWhenRootEffortIsChanged() {
		project.getEffort().setDeclared(60);
		EffortInferenceEngine.process(project);

		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 3)));
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort() {
		final Scope a21 = project.getChild(0).getChild(1).getChild(0);
		a21.getEffort().setDeclared(7);
		EffortInferenceEngine.process(a21.getParent());

		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 4)));
	}

}
