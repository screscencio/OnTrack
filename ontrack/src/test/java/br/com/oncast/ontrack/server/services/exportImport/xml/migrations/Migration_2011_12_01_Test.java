package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertAttributeExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.getElements;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.readXMLFromFile;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2011_12_01_Test {

	private Document sourceDocument;
	private Migration migration;

	@Before
	public void setUp() throws Exception {
		sourceDocument = readXMLFromFile("ontrack_2011_11_18.xml");
		migration = new Migration_2011_12_01();
	}

	@Test
	public void shouldAddIdForEachUser() throws Exception {
		final String xPath = "/ontrackXML/users/user/@id";
		assertAttributeDoesntExist(sourceDocument, xPath);

		migration.apply(sourceDocument);
		assertAttributeExists(sourceDocument, xPath);
	}

	@Test
	public void shouldAddIdForEachProject() throws Exception {
		final String xPath = "/ontrackXML/projects/project/@id";
		assertAttributeDoesntExist(sourceDocument, xPath);

		migration.apply(sourceDocument);
		assertAttributeExists(sourceDocument, xPath);
	}

	@Test
	public void shouldCreateAuthorizationList() throws Exception {
		final String xPath = "/ontrackXML/projectAuthorizations";
		assertElementDoesntExist(sourceDocument, xPath);

		migration.apply(sourceDocument);
		assertElementExists(sourceDocument, xPath);
	}

	@Test
	public void shouldAssociateAllUsersWithAllProjectsInProjectAuthorization() throws Exception {
		final String xPath = "/ontrackXML/projectAuthorizations/projectAuthorization";
		assertElementDoesntExist(sourceDocument, xPath);

		migration.apply(sourceDocument);

		final List<Element> userList = getElements(sourceDocument, "/ontrackXML/users/user");
		final List<Element> projectList = getElements(sourceDocument, "/ontrackXML/projects/project");

		for (final Element project : projectList) {
			for (final Element user : userList) {
				assertElementExists(sourceDocument, xPath + "[@userId='" + user.attributeValue("id") + "' and @projectId='" + project.attributeValue("id")
						+ "']");
			}
		}
	}
}
