package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import org.dom4j.Document;

import br.com.oncast.ontrack.server.services.exportImport.xml.MigrationExecuter;
import br.com.oncast.ontrack.server.services.exportImport.xml.MigrationVersionController;

public class OntrackMigrationManager {

	private static final String MIGRATIONS_PACKAGE = "br.com.oncast.ontrack.server.services.exportImport.xml.migrations";

	public static String getCurrentVersion() {
		return new MigrationVersionController(MIGRATIONS_PACKAGE).getLatestMigrationVersion();
	}

	public static void applyMigrationsOn(final Document document) throws Exception {
		new MigrationExecuter(MIGRATIONS_PACKAGE).executeMigrations(document);
	}

}
