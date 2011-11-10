package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Test;

public class MigrationExecuterTest2 {

	private static final String MIGRATIONS_PACKAGE_NAME = "br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations";

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

}
