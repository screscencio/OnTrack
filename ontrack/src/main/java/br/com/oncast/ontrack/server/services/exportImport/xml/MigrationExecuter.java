package br.com.oncast.ontrack.server.services.exportImport.xml;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.server.services.metrics.ServerAnalytics;

import java.util.Date;

import org.dom4j.Attribute;
import org.dom4j.Document;

public class MigrationExecuter {

	private final MigrationVersionController migrationVersionController;
	private final ServerAnalytics serverAnalytics;

	public MigrationExecuter(final String migrationPackageName, final ServerAnalytics serverAnalytics) {
		this.serverAnalytics = serverAnalytics;
		migrationVersionController = new MigrationVersionController(migrationPackageName);
	}

	public void executeMigrations(final Document document) throws Exception {
		final long initialTimestamp = new Date().getTime();
		final String version = getVersionFrom(document);
		String destinationVersion = "No Migration";
		for (final Migration migration : migrationVersionController.findMigrationsAfter(version)) {
			migration.apply(document);
			destinationVersion = migration.getVersion();
		}
		final long time = new Date().getTime() - initialTimestamp;
		serverAnalytics.onMigrationExecution(version, destinationVersion, time);
	}

	private String getVersionFrom(final Document document) {
		final Attribute version = document.getRootElement().attribute("version");
		return version.getValue();
	}
}
