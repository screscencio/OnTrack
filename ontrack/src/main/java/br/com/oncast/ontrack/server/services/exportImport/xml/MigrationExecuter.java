package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.reflections.Reflections;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class MigrationExecuter {

	private static final char FILE_EXTENSION_SEPARATOR = '.';
	private static final String FILE_EXTENSION = ".xml";
	private static final String PACKAGE_ENCLOSING = ".";
	private static final String DEFAULT_VERSION = "0";
	private static final String MIGRATED_FILE_POSTFIX = "migrated";
	private final String migrationsPrefix;

	public MigrationExecuter(final String packageName) {
		this.migrationsPrefix = packageName + PACKAGE_ENCLOSING;
	}

	public void executeMigrations(final String sourceXMLName) throws Exception {
		final Document document = read(sourceXMLName);
		final String date = getVersionFrom(document);
		for (final Migration migration : findMigrationsAfter(date)) {
			migration.apply(document);
		}
		write(document, getMigratedName(document));

	}

	public String getLatestVersion() {
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
		final Attribute version = (Attribute) document.selectObject("/ontrackXML/@version");
		return version.getValue();
	}

	private String getMigratedName(final Document document) {
		String name = document.getName();
		name = name.substring(0, name.lastIndexOf(FILE_EXTENSION_SEPARATOR));
		return name + FILE_EXTENSION_SEPARATOR + MIGRATED_FILE_POSTFIX + FILE_EXTENSION;
	}

	private Document read(final String fileName) throws DocumentException {
		final SAXReader reader = new SAXReader();
		return reader.read(fileName);
	}

	private void write(final Document document, final String outPutName) throws IOException {
		writeToFile(document, outPutName);

		printOnConsole(document);
	}

	private void printOnConsole(final Document document) throws UnsupportedEncodingException, IOException {
		final OutputFormat format = OutputFormat.createPrettyPrint();
		new XMLWriter(System.out, format).write(document);
	}

	private void writeToFile(final Document document, final String outPutName) throws IOException {
		final XMLWriter writer = new XMLWriter(new FileWriter(outPutName));
		writer.write(document);
		writer.close();
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
