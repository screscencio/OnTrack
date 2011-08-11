package br.com.oncast.ontrack.shared.model.effort;

import java.io.File;

import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindImporter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceTestUtils {
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
		final FreeMindImporter importer = FreeMindImporter.importMapFrom(new File("src/test/java/br/com/oncast/ontrack/shared/model/effort/"
				+ fileName + ".mm"));

		return importer.getScope();
	}
}
