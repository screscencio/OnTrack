package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

public class XMLImporterService {

	private final PersistenceService persistenceService;

	public XMLImporterService(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void importFromFile(final File file) {
		final XMLImporter xmlImporter = new XMLImporter(persistenceService);
		xmlImporter.loadXML(file).persistObjects();
	}
}
