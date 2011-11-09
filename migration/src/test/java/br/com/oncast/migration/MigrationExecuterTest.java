package br.com.oncast.migration;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.oncast.migration.sample.migrations.Migration_2011_10_01;
import br.com.oncast.migration.sample.migrations.Migration_2011_10_05;
import br.com.oncast.migration.sample.migrations.Migration_2011_10_10;
import br.com.oncast.migration.sample.migrations2.Migration2_2011_08_01;
import br.com.oncast.migration.sample.migrations2.Migration2_2011_08_05;
import br.com.oncast.migration.sample.migrations2.subPackage.Migration2_2011_08_10;


//TODO++: test the execution of the migrations , now it is testing only the search mechanism
public class MigrationExecuterTest {
	
	private static String MIGRATIONS_PACKAGE_NAME = "br.com.oncast.migration.sample.migrations";
	
	@Test
	public void shouldFindThreeNeededMigrationsAfterThisDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		assertEquals(3, executer.findMigrationsAfter("2011_09_29").size());
	}

	@Test
	public void shouldFindTheRightThreeNeededMigrationsAfterAGivenDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_09_29");
		
		assertEquals(3, neededMigrations.size());
		
		assertTrue(neededMigrations.contains(new Migration_2011_10_01()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}
	
	@Test
	public void shouldFindTheRightTwoNeededMigrationsAfterAGivenDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_10_02");
		
		assertEquals(2, neededMigrations.size());
		
		assertFalse(neededMigrations.contains(new Migration_2011_10_01()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}
	@Test
	public void theMigrationWithSameDateOfTheGivenDateShouldNotBeNeeded() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_10_05");
		
		assertEquals(1, neededMigrations.size());
		
		assertFalse(neededMigrations.contains(new Migration_2011_10_01()));
		assertFalse(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}
	
	@Test
	public void shouldFindNeededMigrationsInOrder() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_09_29");

		assertEquals(3, neededMigrations.size());
		
		assertEquals(new Migration_2011_10_01(), neededMigrations.get(0));
		assertEquals(new Migration_2011_10_05(), neededMigrations.get(1));
		assertEquals(new Migration_2011_10_10(), neededMigrations.get(2));
	}
	
	@Test
	public void shouldNotFindNeededMigrationsOfOtherPackages() throws Exception {
		MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_09_29");
		
		assertEquals(3, neededMigrations.size());
		
		assertTrue(neededMigrations.contains(new Migration_2011_10_01()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
		
		assertFalse(neededMigrations.contains(new Migration2_2011_08_05()));
		assertFalse(neededMigrations.contains(new Migration2_2011_08_10()));
		assertFalse(neededMigrations.contains(new Migration2_2011_08_01()));
	}
	
	@Test
	public void shouldFindNeededMigrationsOfTheGivenPackageAndSubPackages() throws Exception {
		final String packageName = "br.com.oncast.migration.sample.migrations2";
		MigrationExecuter executer = new MigrationExecuter(packageName);
		List<Migration> neededMigrations = executer.findMigrationsAfter("2011_08_03");
		
		assertEquals(4, neededMigrations.size());
		
		assertEquals(new Migration2_2011_08_05(), neededMigrations.get(0));
		assertEquals(new Migration2_2011_08_10(), neededMigrations.get(1));
	}
	

}
