package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindImporter;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.inference.InferenceOverScopeEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProgressInferenceTestUtils {

	private static final List<InferenceOverScopeEngine> inferenceEngines = new ArrayList<InferenceOverScopeEngine>();

	static {
		inferenceEngines.add(new EffortInferenceEngine());
		inferenceEngines.add(new ProgressInferenceEngine());
	}

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
		final FreeMindImporter importer = FreeMindImporter.importMapFrom(new File("src/test/java/br/com/oncast/ontrack/shared/model/progress/"
				+ fileName + ".mm"));

		return importer.getScope();
	}
}
