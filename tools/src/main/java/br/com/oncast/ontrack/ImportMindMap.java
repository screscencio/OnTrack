package br.com.oncast.ontrack;

import java.io.File;

import br.com.oncast.ontrack.server.services.ProjectPrinter;
import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindProjectLoader;
import br.com.oncast.ontrack.shared.model.project.Project;

public class ImportMindMap {
	public static void main(final String... args) {
		final File current = new File(".");
		for (final File file : current.listFiles()) {
			if (!file.getName().endsWith(".mm")) continue;
			importMindMap(file);
			break;
		}
	}

	private static void importMindMap(final File mmFile) {
		final Project project = FreeMindProjectLoader.loadMap(mmFile);
//		final List<ModelAction> actions = ProjectActionsAssembler.assemble(project);
		ProjectPrinter.print(project);
	}
}
