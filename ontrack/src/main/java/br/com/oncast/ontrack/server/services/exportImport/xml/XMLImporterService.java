package br.com.oncast.ontrack.server.services.exportImport.xml;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

import java.io.File;

public class XMLImporterService {

	private final PersistenceService persistenceService;
	private final BusinessLogic businessLogic;

	public XMLImporterService(final PersistenceService persistenceService, final BusinessLogic businessLogic) {
		this.persistenceService = persistenceService;
		this.businessLogic = businessLogic;
	}

	public void importFromFile(final File file) {
		final XMLImporter xmlImporter = new XMLImporter(persistenceService, businessLogic);
		xmlImporter.loadXML(file).persistObjects().loadProjects();
	}
}
