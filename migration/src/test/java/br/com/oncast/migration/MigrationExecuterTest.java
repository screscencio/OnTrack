package br.com.oncast.migration;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.oncast.migration.test.migrations.Migration_2011_10_01_14_32_11;
import br.com.oncast.migration.test.migrations.Migration_2011_10_05_18_00_00;
import br.com.oncast.migration.test.migrations.Migration_2011_10_10_08_15_39;
import br.com.oncast.migration.test.migrations2.Migration2_2011_08_01_14_32_11;
import br.com.oncast.migration.test.migrations2.Migration2_2011_08_05_18_00_00;
import br.com.oncast.migration.test.migrations2.subPackage.Migration2_2011_08_10_08_15_39;


public class MigrationExecuterTest {
	
	private static String MIGRATIONS_PACKAGE_NAME = "br.com.oncast.migration.test.migrations";
	
	@Test
	public void shouldFindThreeNeededMigrationsAfterThisDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		assertEquals(3, executer.findNeededMigrations("2011_10_01_14_32_10").size());
	}

	@Test
	public void shouldFindTheRightThreeNeededMigrationsAfterAGivenDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_10_01_14_32_10");
		
		assertEquals(3, neededMigrations.size());
		
		assertTrue(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
	}
	
	@Test
	public void shouldFindTheRightTwoNeededMigrationsAfterAGivenDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_10_02_14_00_00");
		
		assertEquals(2, neededMigrations.size());
		
		assertFalse(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
	}
	@Test
	public void theMigrationWithSameDateOfTheGivenDateShouldNotBeNeeded() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_10_05_18_00_00");
		
		assertEquals(1, neededMigrations.size());
		
		assertFalse(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertFalse(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
	}
	
	@Test
	public void shouldFindNeededMigrationsInOrder() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_10_01_14_32_10");

		assertEquals(3, neededMigrations.size());
		
		assertEquals(new Migration_2011_10_01_14_32_11(), neededMigrations.get(0));
		assertEquals(new Migration_2011_10_05_18_00_00(), neededMigrations.get(1));
		assertEquals(new Migration_2011_10_10_08_15_39(), neededMigrations.get(2));
	}
	
	@Test
	public void shouldNotFindNeededMigrationsOfOtherPackages() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_10_01_14_32_10");
		
		assertEquals(3, neededMigrations.size());
		
		assertTrue(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
		
		assertFalse(neededMigrations.contains(new Migration2_2011_08_05_18_00_00()));
		assertFalse(neededMigrations.contains(new Migration2_2011_08_10_08_15_39()));
		assertFalse(neededMigrations.contains(new Migration2_2011_08_01_14_32_11()));
	}
	
	@Test
	public void shouldFindNeededMigrationsOfTheGivenPackageAndSubPackages() throws Exception {
		final String packageName = "br.com.oncast.migration.test.migrations2";
		MigrationExecuter executer = new MigrationExecuter(packageName);
		List<Migration> neededMigrations = executer.findNeededMigrations("2011_08_03_03_20_01");
		
		assertEquals(2, neededMigrations.size());
		
		assertEquals(new Migration2_2011_08_05_18_00_00(), neededMigrations.get(0));
		assertEquals(new Migration2_2011_08_10_08_15_39(), neededMigrations.get(1));
	}

}
