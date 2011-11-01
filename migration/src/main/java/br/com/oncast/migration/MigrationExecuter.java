package br.com.oncast.migration;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class MigrationExecuter {

	public static void main(String[] args) throws Exception {
		File dir = new File("src/migrations");
		for (String migrationFileName : dir.list()) {
			if (migrationFileName.equals("Migration1.java")) executeMigration(migrationFileName);
		}
	}

	private static void executeMigration(String migrationFileName) throws Exception {
		Migration migrationInstance = (Migration) Class.forName("migrations.Migration1").newInstance();
		
		Document document = readSource();
		migrationInstance.execute(document);
	}

	private static Document readSource() throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read("original.xml");
	}

	private ArrayList<Migration> allMigrations;
	
	public MigrationExecuter() {
		allMigrations.add(new Migration_2011_10_01_14_32_11());
		allMigrations.add(new Migration_2011_10_05_18_00_00());
		allMigrations.add(new Migration_2011_10_10_08_15_39());
	}

	public List<Migration> findNeededMigrations(String date) {
		ArrayList<Migration> migrations = new ArrayList<Migration>();
		for (Migration migration : allMigrations) {
			if (isMigrationBefore(migration, date))
			migrations.add(migration);
		}
		return migrations;
	}

	private boolean isMigrationBefore(Migration migration, String date) {
		String migrationDate = extractDate(migration);
		return migrationDate.compareTo(date) > 0;
	}

	private String extractDate(Migration migration) {
		String name = migration.getClass().getSimpleName();
		name.indexOf('_');
		return name;
	}
}
