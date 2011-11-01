package br.com.oncast.migration;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.junit.Test;


public class MigrationExecuterTest {
	
	@Test
	public void shouldFindThreeNeededMigrationsAfterThisDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter();
		assertEquals(3, executer.findNeededMigrations("2011-10-01_14:32:10").size());
	}

	@Test
	public void shouldFindTheRightThreeNeededMigrationsAfterThisDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter();
		List<Migration> neededMigrations = executer.findNeededMigrations("2011-10-01_14:32:10");
		assertTrue(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
	}
	
	@Test
	public void shouldFindTheRightTwoNeededMigrationsAfterThisDate() throws Exception {
		MigrationExecuter executer = new MigrationExecuter();
		List<Migration> neededMigrations = executer.findNeededMigrations("2011-10-02_14:00:00");
		assertEquals(2, executer.findNeededMigrations("2011-10-01_14:32:10").size());
		assertFalse(neededMigrations.contains(new Migration_2011_10_01_14_32_11()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05_18_00_00()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10_08_15_39()));
	}
}
