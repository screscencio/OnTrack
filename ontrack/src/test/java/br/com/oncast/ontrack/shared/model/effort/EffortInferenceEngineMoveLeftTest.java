package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class EffortInferenceEngineMoveLeftTest {

	final String FILE_NAME_PREFIX = "Project1";
	final Scope original = getOriginalScope(FILE_NAME_PREFIX);

	@Test
	public void shouldApplyInferenceWhenMoveScopeLeft() throws UnableToCompleteActionException {

		final Scope scope = original.getChild(0).getChild(1);
		final ScopeMoveLeftAction moveLeftAction = new ScopeMoveLeftAction(scope.getId());

		moveLeftAction.execute(new ProjectContext(new Project(original, null)));
		new EffortInferenceEngine().process(scope.getParent());

		assertDeepEquals(original, getModifiedScope(FILE_NAME_PREFIX, 1));
	}

}
