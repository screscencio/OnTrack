package br.com.oncast.migration;
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



public class MigrationExecuter {

	private static final String MIGRATED = "migrated";
	private final String packageName;

	public MigrationExecuter(String packageName) {
		this.packageName = packageName;
	}

	public void executeMigrations(String sourceXMLName) throws Exception {
		Document document = read(sourceXMLName);
		String date  = getVersionFrom(document);
		for (Migration migration : findMigrationsAfter(date)) {
			migration.apply(document);
		}
		write(document, getMigratedName(document));
		
	}
	
	public List<Migration> findMigrationsAfter(String version) {
		ArrayList<Migration> migrations = new ArrayList<Migration>();
		
		for (Migration migration : getAllMigrations()) {
			if (isMigrationBefore(migration, version))
			migrations.add(migration);
		}
		return migrations;
	}
	
	private String getVersionFrom(Document document) {
		Attribute version = (Attribute) document.selectObject("/ontrackXML/@version");
		return version.getValue();
	}

	private String getMigratedName(Document document) {
		String name = document.getName();
		name = name.substring(0, name.lastIndexOf('.'));
		return name + "." + MIGRATED + ".xml";
	}

	private Document read(String fileName) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(fileName);
	}
	
	private void write(Document document, String outPutName) throws IOException {
		writeToFile(document, outPutName);
	
	    printOnConsole(document);
	}

	private void printOnConsole(Document document) throws UnsupportedEncodingException, IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		new XMLWriter( System.out, format ).write( document );
	}

	private void writeToFile(Document document, String outPutName) throws IOException {
		XMLWriter writer = new XMLWriter(new FileWriter(outPutName));
	    writer.write( document );
	    writer.close();
	}
	
	private boolean isMigrationBefore(Migration migration, String date) {
		return migration.getVersion().compareTo(date) > 0;
	}

	private ArrayList<Migration> getAllMigrations() {
		Set<Class<? extends Migration>> subTypes = new Reflections(packageName+".").getSubTypesOf(Migration.class);

		ArrayList<Migration> migrations = new ArrayList<Migration>();
		for (Class<? extends Migration> clazz : subTypes) {
			try {
				migrations.add(clazz.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		Collections.sort(migrations);
		return migrations;
	}
}
