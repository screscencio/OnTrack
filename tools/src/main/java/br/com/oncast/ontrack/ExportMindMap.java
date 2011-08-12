package br.com.oncast.ontrack;

import java.io.FileOutputStream;

import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindExporter;
import br.com.oncast.ontrack.shared.model.project.Project;

public class ExportMindMap {
	public static void main(String... args) throws Exception {
		final Project project = ServerBusinessLogicLocator.getInstance().getBusinessLogic().loadProject();
		FileOutputStream fos = new FileOutputStream(project.getProjectScope().getDescription() + ".mm");
		FreeMindExporter.export(project, fos);
		fos.close();
	}
}
