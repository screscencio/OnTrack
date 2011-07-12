package br.com.oncast.ontrack.shared.model.effort.inferenceengine;

import java.io.File;

import br.com.oncast.ontrack.server.util.mindmapconverter.MindMapConverter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class Util {
	static Scope getOriginalScope(final String fileName) {
		return getScope(fileName + " - original");
	}

	static Scope getModifiedScope(final String fileName) {
		return getScope(fileName + " - modified");
	}

	static Scope getModifiedScope(final String fileName, final int version) {
		return getScope(fileName + " - modified" + version);
	}

	static Scope getScope(final String fileName) {
		return MindMapConverter.convert(new File("src/test/java/br/com/oncast/ontrack/shared/model/effort/inferenceengine/" + fileName + ".mm"));
	}
}
