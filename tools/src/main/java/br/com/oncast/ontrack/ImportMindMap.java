package br.com.oncast.ontrack;

import java.io.File;

import br.com.oncast.ontrack.server.services.ProjectPrinter;
import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindProjectLoader;
import br.com.oncast.ontrack.shared.model.project.Project;

public class ImportMindMap {
	public static void main(String... args) {
		File current = new File(".");
		for (File file : current.listFiles()) {
			if (!file.getName().endsWith(".mm")) continue;
			importMindMap(file);
			break;
		}
	}

	private static void importMindMap(File mmFile) {
		Project project = FreeMindProjectLoader.loadMap(mmFile);
		ProjectPrinter.print(project);
	}
}
