package br.com.oncast.ontrack.shared.model.effort;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import br.com.oncast.ontrack.server.util.mindmapconverter.MindMapConverter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngineTest {

	@Test
	public void shouldChangeParentsEffortIfTheirEffortIsDifferent() {
		final String fileName = "Scope hierarchy 1";
		final Scope original = getOriginalScope(fileName);
		final Scope modified = getModifiedScope(fileName);

		final Scope scope = original.getChild(1).getChild(1);
		scope.getEffort().setDeclared(15);
		EffortInferenceEngine.process(scope);

		assertTrue(original.deepEquals(modified));
	}

	private Scope getOriginalScope(final String fileName) {
		return getScope(fileName + " - original");
	}

	private Scope getModifiedScope(final String fileName) {
		return getScope(fileName + " - modified");
	}

	private Scope getScope(final String fileName) {
		return MindMapConverter.convert(new File("src/test/java/br/com/oncast/ontrack/shared/model/effort/" + fileName + ".mm"));
	}
}
