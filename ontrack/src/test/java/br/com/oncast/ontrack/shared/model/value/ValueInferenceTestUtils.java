package br.com.oncast.ontrack.shared.model.value;

import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindImporter;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import java.io.File;

public class ValueInferenceTestUtils {
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
		final FreeMindImporter importer = FreeMindImporter.importMapFrom(new File("src/test/java/br/com/oncast/ontrack/shared/model/value/"
				+ fileName + ".mm"));

		return importer.getScope();
	}
}
