package br.com.oncast.ontrack.server.services.exportImport.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class MigrationExecuter {

	private final MigrationVersionController migrationVersionController;

	public MigrationExecuter(final String migrationPackageName) {
		migrationVersionController = new MigrationVersionController(migrationPackageName);
	}

	public void executeMigrations(final Document document) throws Exception {
		final String version = getVersionFrom(document);
		for (final Migration migration : migrationVersionController.findMigrationsAfter(version)) {
			migration.apply(document);
		}
	}

	private String getVersionFrom(final Document document) {
		final Attribute version = document.getRootElement().attribute("version");
		return version.getValue();
	}
}
