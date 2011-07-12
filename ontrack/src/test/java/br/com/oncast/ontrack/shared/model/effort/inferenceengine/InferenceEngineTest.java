package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.inferenceengine.Util.getOriginalScope;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class InferenceEngineTest {

	@Test
	public void shouldApplyInferenceWhenMoveScopeLeft() throws UnableToCompleteActionException {
		final String fileName = "Project1";
		final Scope original = getOriginalScope(fileName);

		final Scope scope = original.getChild(0).getChild(1);
		final ScopeMoveLeftAction moveLeftAction = new ScopeMoveLeftAction(scope.getId());
		moveLeftAction.execute(new ProjectContext(new Project(original, null)));

		EffortInferenceEngine.process(scope);

		assertTrue(original.deepEquals(getModifiedScope(fileName)));
	}

}
