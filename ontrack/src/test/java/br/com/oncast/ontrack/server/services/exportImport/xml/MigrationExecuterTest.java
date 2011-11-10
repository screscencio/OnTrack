package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations.Migration_2011_10_01;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations.Migration_2011_10_05;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations.Migration_2011_10_10;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.Migration2_2011_08_01;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.Migration2_2011_08_05;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.subPackage.Migration2_2011_08_10;
import br.com.oncast.ontrack.utils.TestUtils;

public class MigrationExecuterTest {

	private static final String MIGRATIONS_PACKAGE_NAME = "br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations";
	private static final String MIGRATIONS2_PACKAGE_NAME = "br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2";

	@Test
	public void shouldFindThreeNeededMigrationsAfterThisDate() throws Exception {

		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		assertEquals(3, findMigrationsAfter(executer, "2011_09_29").size());
	}

	@Test
	public void shouldFindTheRightThreeNeededMigrationsAfterAGivenDate() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_09_29");

		assertEquals(3, neededMigrations.size());

		assertTrue(neededMigrations.contains(new Migration_2011_10_01()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}

	@Test
	public void shouldFindTheRightTwoNeededMigrationsAfterAGivenDate() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_10_02");

		assertEquals(2, neededMigrations.size());

		assertFalse(neededMigrations.contains(new Migration_2011_10_01()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}

	@Test
	public void theMigrationWithSameDateOfTheGivenDateShouldNotBeNeeded() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_10_05");

		assertEquals(1, neededMigrations.size());

		assertFalse(neededMigrations.contains(new Migration_2011_10_01()));
		assertFalse(neededMigrations.contains(new Migration_2011_10_05()));
		assertTrue(neededMigrations.contains(new Migration_2011_10_10()));
	}

	@Test
	public void shouldFindNeededMigrationsInOrder() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_09_29");

		assertEquals(3, neededMigrations.size());

		assertEquals(new Migration_2011_10_01(), neededMigrations.get(0));
		assertEquals(new Migration_2011_10_05(), neededMigrations.get(1));
		assertEquals(new Migration_2011_10_10(), neededMigrations.get(2));
	}

	@Test
	public void shouldNotFindNeededMigrationsOfOtherPackages() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_09_29");

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
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS2_PACKAGE_NAME);
		final List<Migration> neededMigrations = findMigrationsAfter(executer, "2011_08_03");

		assertEquals(4, neededMigrations.size());

		assertEquals(new Migration2_2011_08_05(), neededMigrations.get(0));
		assertEquals(new Migration2_2011_08_10(), neededMigrations.get(1));
	}

	@Test
	public void theLastVersionOfTheMigrationsShouldBeZeroWhenNoMigrationWasFoundOnTheGivenPackage() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter("package.without.migrations");
		final String version = executer.getLatestMigrationVersion();
		assertEquals("0", version);
	}

	@Test
	public void theLastVersionOfTheMigrationsShouldBeTheLatestMigrationDateOnAGivenPackage() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final String version = executer.getLatestMigrationVersion();
		assertEquals("2011_10_10", version);
	}

	@Test
	public void theVersionAttributeValueOfRootElementShouldBeTheDocumentVersion() throws Exception {
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		final Document document = DocumentHelper.parseText("<root version=\"2011_10_10\"></root>");
		assertEquals("2011_10_10", getVersionFrom(executer, document));
	}

	@Test
	public void shouldExecuteCorrectMigrations() throws Exception {
		final Document document = DocumentHelper.parseText("<root version=\"2011_10_01\"></root>");
		final MigrationExecuter executer = new MigrationExecuter(MIGRATIONS_PACKAGE_NAME);
		executer.executeMigrations(document);

		assertEquals(0, document.selectNodes("root/Migration_2011_10_01").size());
		assertEquals(1, document.selectNodes("root/Migration_2011_10_05").size());
		assertEquals(1, document.selectNodes("root/Migration_2011_10_10").size());
	}

	private String getVersionFrom(final MigrationExecuter executer, final Document document) throws Exception {
		final Method method = MigrationExecuter.class.getDeclaredMethod("getVersionFrom", Document.class);
		method.setAccessible(true);
		return (String) method.invoke(executer, document);
	}

	@SuppressWarnings("unchecked")
	private List<Migration> findMigrationsAfter(final MigrationExecuter executer, final String version) throws Exception {
		return (List<Migration>) TestUtils.callPrivateMethod(executer, "findMigrationsAfter", version);
	}
}
