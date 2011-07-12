package br.com.oncast.ontrack.shared.model.effort;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import br.com.oncast.ontrack.server.util.mindmapconverter.MindMapConverter;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class EffortInferenceEngineTest {

	@Test
	public void shouldApplyInferencesWhenEffortChanges() {
		final String fileName = "Project1";
		final Scope project = getOriginalScope(fileName);

		project.getEffort().setDeclared(30);
		EffortInferenceEngine.process(project);
		assertTrue(project.deepEquals(getModifiedScope(fileName, 1)));

		final Scope a2 = project.getChild(0).getChild(1);
		a2.getEffort().setDeclared(10);
		EffortInferenceEngine.process(a2);
		final Scope modifiedScope = getModifiedScope(fileName, 2);
		assertTrue(project.deepEquals(modifiedScope));

		project.getEffort().setDeclared(60);
		EffortInferenceEngine.process(project);
		assertTrue(project.deepEquals(getModifiedScope(fileName, 3)));

		final Scope a21 = a2.getChild(0);
		a21.getEffort().setDeclared(7);
		EffortInferenceEngine.process(a21);
		assertTrue(project.deepEquals(getModifiedScope(fileName, 4)));
	}

	@Test
	// FIXME
	public void shouldApplyInferenceWhenMoveScopeLeft() throws UnableToCompleteActionException {
		final String fileName = "Scope hierarchy 2";
		final Scope original = getOriginalScope(fileName);

		final Scope scope = original.getChild(1).getChild(1);
		final ScopeMoveLeftAction moveLeftAction = new ScopeMoveLeftAction(scope.getId());
		moveLeftAction.execute(new ProjectContext(new Project(original, null)));

		EffortInferenceEngine.process(scope);

		assertTrue(original.deepEquals(getModifiedScope(fileName)));
	}

	private Scope getOriginalScope(final String fileName) {
		return getScope(fileName + " - original");
	}

	private Scope getModifiedScope(final String fileName) {
		return getScope(fileName + " - modified");
	}

	private Scope getModifiedScope(final String fileName, final int version) {
		return getScope(fileName + " - modified" + version);
	}

	private Scope getScope(final String fileName) {
		return MindMapConverter.convert(new File("src/test/java/br/com/oncast/ontrack/shared/model/effort/" + fileName + ".mm"));
	}
}
