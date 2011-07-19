package br.com.oncast.ontrack.utils;

import java.io.File;

import br.com.oncast.ontrack.server.util.mindmapconverter.freemind.FreeMindMap;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter.FreeMindImporter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class TestUtils {
	public static Scope getOriginalScope(final String fileName) {
		return getScope(fileName + " - original");
	}

	public static Scope getModifiedScope(final String fileName) {
		return getScope(fileName + " - modified");
	}

	public static Scope getModifiedScope(final String fileName, final int version) {
		return getScope(fileName + " - modified" + version);
	}

	public static Scope getScope(final String fileName) {
		final FreeMindMap mindMap = FreeMindMap.open(new File("src/test/java/br/com/oncast/ontrack/shared/model/effort/inferenceengine/" + fileName + ".mm"));
		final FreeMindImporter importer = FreeMindImporter.interpret(mindMap);

		return importer.getScope();
	}
}
