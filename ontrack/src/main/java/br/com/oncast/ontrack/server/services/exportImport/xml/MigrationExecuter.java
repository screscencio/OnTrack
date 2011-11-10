package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.reflections.Reflections;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class MigrationExecuter {

	private static final String PACKAGE_ENCLOSING = ".";
	private static final String DEFAULT_VERSION = "0";
	private final String migrationsPrefix;

	public MigrationExecuter(final String packageName) {
		this.migrationsPrefix = packageName + PACKAGE_ENCLOSING;
	}

	public void executeMigrations(final Document document) throws Exception {
		final String version = getVersionFrom(document);
		for (final Migration migration : findMigrationsAfter(version)) {
			migration.apply(document);
		}
	}

	public String getLatestMigrationVersion() {
		final ArrayList<Migration> migrations = getAllMigrations();
		return migrations.isEmpty() ? DEFAULT_VERSION : getLastOf(migrations).getVersion();
	}

	private List<Migration> findMigrationsAfter(final String version) {
		final ArrayList<Migration> migrations = new ArrayList<Migration>();

		for (final Migration migration : getAllMigrations()) {
			if (isMigrationBefore(migration, version)) migrations.add(migration);
		}
		return migrations;
	}

	private String getVersionFrom(final Document document) {
		final Attribute version = document.getRootElement().attribute("version");
		return version.getValue();
	}

	private boolean isMigrationBefore(final Migration migration, final String date) {
		return migration.getVersion().compareTo(date) > 0;
	}

	private ArrayList<Migration> getAllMigrations() {
		final Set<Class<? extends Migration>> subTypes = new Reflections(migrationsPrefix).getSubTypesOf(Migration.class);

		final ArrayList<Migration> migrations = new ArrayList<Migration>();
		for (final Class<? extends Migration> clazz : subTypes) {
			try {
				migrations.add(clazz.newInstance());
			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}

		Collections.sort(migrations);
		return migrations;
	}

	private Migration getLastOf(final ArrayList<Migration> migrations) {
		return migrations.get(migrations.size() - 1);
	}
}
