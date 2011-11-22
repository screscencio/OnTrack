package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementAttributeValueIs;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementDoesntExist;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementExists;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementHasTheseAttributesAndNothingElse;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.assertElementsType;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.getElement;
import static br.com.oncast.ontrack.server.util.migration.MigrationTestUtils.readXMLFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

@SuppressWarnings("rawtypes")
public class Migration_2011_11_01_Test {

	private Document sourceDocument;
	private Migration migration;

	@Before
	public void setUp() throws Exception {
		sourceDocument = readXMLFromFile("ontrack_2011_10_01.xml");
		migration = new Migration_2011_11_01();
	}

	@Test
	public void shouldAddUsersListToRootElement() throws Exception {
		assertElementDoesntExist(sourceDocument, "//users");
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, "//users");
		assertElementsType(sourceDocument, ArrayList.class, "//users");
	}

	@Test
	public void shouldAddDefaultUser() throws Exception {
		assertElementDoesntExist(sourceDocument, "//user");
		migration.apply(sourceDocument);

		final String userXPath = "//user";
		assertElementExists(sourceDocument, userXPath);

		final Element user = getElement(sourceDocument, userXPath);
		assertElementHasTheseAttributesAndNothingElse(user, "id", "email");
		assertElementAttributeValueIs(user, "id", "1");
		assertElementAttributeValueIs(user, "email", "admin@ontrack.com");
	}

	@Test
	public void shouldAddPasswordsListToRootElement() throws Exception {
		assertElementDoesntExist(sourceDocument, "//passwords");
		migration.apply(sourceDocument);

		assertElementExists(sourceDocument, "//passwords");
		assertElementsType(sourceDocument, ArrayList.class, "//passwords");
	}

	@Test
	public void shouldAddDefaultPassword() throws Exception {
		assertElementDoesntExist(sourceDocument, "//password");
		migration.apply(sourceDocument);

		final String passwordXPath = "//password";
		assertElementExists(sourceDocument, passwordXPath);

		final Element password = getElement(sourceDocument, passwordXPath);
		assertElementHasTheseAttributesAndNothingElse(password, "id", "passwordHash", "passwordSalt", "userId");

		assertElementAttributeValueIs(password, "id", "1");
		assertElementAttributeValueIs(password, "userId", "1");
		assertElementAttributeValueIs(password, "passwordHash", "FfWz98wvWdj1OvOFWIO1dNVXJC0=");
		assertElementAttributeValueIs(password, "passwordSalt", "Q7wT3NyAloU=");
	}

	@Test
	public void shouldAddTimestampAttributeToAllScopeDeclareProgressActions() throws Exception {
		assertAnyScopeDeclareProgressActionDontHaveTimestampAttribute();
		migration.apply(sourceDocument);

		final List actions = sourceDocument.selectNodes("//*[@class='br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction']");
		final Iterator iterator = actions.iterator();

		while (iterator.hasNext()) {
			final Element action = (Element) iterator.next();
			assertNotNull(action.attribute("timestamp"));
		}
	}

	@Test
	public void theTimestampOfScopeDeclareProgressActionShouldBeTheSameAsTheirUserAction() throws Exception {
		assertAnyScopeDeclareProgressActionDontHaveTimestampAttribute();
		migration.apply(sourceDocument);

		final List actions = sourceDocument.selectNodes("//*[@class='br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction']");
		final Iterator iterator = actions.iterator();

		while (iterator.hasNext()) {
			final Element action = (Element) iterator.next();
			final String timestamp = action.attributeValue("timestamp");

			final Element ancestor = (Element) action.selectObject("ancestor::userAction");

			assertEquals(ancestor.attributeValue("timestamp"), timestamp);
		}
	}

	private void assertAnyScopeDeclareProgressActionDontHaveTimestampAttribute() throws DocumentException {
		final List actions = sourceDocument.selectNodes("//*[@class='br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction'][@timestamp]");
		assertTrue(actions.isEmpty());
	}
}
