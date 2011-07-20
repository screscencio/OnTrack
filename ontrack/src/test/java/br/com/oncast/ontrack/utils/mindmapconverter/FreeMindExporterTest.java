package br.com.oncast.ontrack.utils.mindmapconverter;

import java.io.File;

import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter.FreeMindExporter;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class FreeMindExporterTest {
	private static final File PROJECT_MM_FILE = new File("/home/jaime/ProjetoTeste.mm");

	@Test
	public void shouldExportMapWithVersion() {
		final Scope scope = ScopeMock.getScope();
		scope.getEffort().setDeclared(100);
		EffortInferenceEngine.process(scope);
		scope.getChild(0).getChild(0).getProgress().setDescription("Under work");

		FreeMindExporter.export(new Project(scope, null), PROJECT_MM_FILE);
	}

}
