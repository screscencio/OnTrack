package br.com.oncast.ontrack.server.services.exportImport.xml;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.services.metrics.ServerAnalytics;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

import java.io.File;

public class XMLImporterService {

	private final PersistenceService persistenceService;
	private final BusinessLogic businessLogic;
	private final ServerAnalytics serverAnalytics;

	public XMLImporterService(final PersistenceService persistenceService, final BusinessLogic businessLogic, final ServerAnalytics serverAnalytics) {
		this.persistenceService = persistenceService;
		this.businessLogic = businessLogic;
		this.serverAnalytics = serverAnalytics;
	}

	public void importFromFile(final File file) {
		final XMLImporter xmlImporter = new XMLImporter(persistenceService, businessLogic, serverAnalytics);
		xmlImporter.loadXML(file).persistObjects().loadProjects();
	}
}
