package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getOriginalScope;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Flow2 {

	private final String FILE_NAME = "Flow2";
	final Scope project = getOriginalScope(FILE_NAME);

	// FIXME Change this class to act like Flow1
	@Test
	public void shouldApplyInferencesWhenEffortChanges() {
		project.getEffort().setDeclared(30);
		EffortInferenceEngine.process(project);
		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 1)));

		final Scope a2 = project.getChild(0).getChild(1);
		a2.getEffort().setDeclared(10);
		EffortInferenceEngine.process(a2);
		final Scope modifiedScope = getModifiedScope(FILE_NAME, 2);
		assertTrue(project.deepEquals(modifiedScope));

		project.getEffort().setDeclared(60);
		EffortInferenceEngine.process(project);
		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 3)));

		final Scope a21 = a2.getChild(0);
		a21.getEffort().setDeclared(7);
		EffortInferenceEngine.process(a21);
		assertTrue(project.deepEquals(getModifiedScope(FILE_NAME, 4)));
	}

}
