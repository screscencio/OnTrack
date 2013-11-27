package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations.Migration_2011_10_01;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.Migration2_2011_08_01;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.Migration2_2011_10_01;
import br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.subPackage.Migration2_2011_11_10;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MigrationTest {

	private static final String testXML =
			"<?xml version=\"1.0\" ?>" +
					"<root version=\"1320175697999\">" +
					"	<users class=\"java.util.ArrayList\">" +
					"		<user id=\"1\" email=\"user1@email\"/>" +
					"		<user id=\"2\" email=\"user2@email\"/>" +
					"	</users>\n" +
					"		<passwords class=\"java.util.ArrayList\">" +
					"		<password id=\"0\" userId=\"1\" passwordHash=\"B2mo6p1KwIpcm_LakZWdWINAkXk=\" passwordSalt=\"58d2kj1EAPc=\"/>" +
					"		<password id=\"0\" userId=\"2\" passwordHash=\"jWZP0fb2I$SX7j8nRFu7eVn28hw=\" passwordSalt=\"kYnCwQfWCZY=\"/>" +
					"	</passwords>" +
					"	<userActions class=\"java.util.ArrayList\">" +
					"		<userAction id=\"1\" timestamp=\"2011-11-01 17:28:17.999 BRST\">" +
					"			<action class=\"java.lang.Object\">" +
					"				<referenceId id=\"3A63F0EF-D5E9-4C9C-9AFC-010033D36BF6\"/>" +
					"			</action>" +
					"		</userAction>" +
					"		<userAction id=\"1\" timestamp=\"2011-11-01 17:28:17.999 BRST\">" +
					"			<action class=\"java.lang.String\" newDescription=\"descricao\">" +
					"				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
					"   			<subActionList class=\"java.util.ArrayList\">" +
					"      				<modelAction class=\"java.lang.String\" newDescription=\"release\">" +
					"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
					"      				</modelAction>" +
					"      				<modelAction class=\"java.util.ArrayList\" newReleaseDescription=\"release\" scopePriority=\"-1\">" +
					"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
					"      				</modelAction>" +
					"      				<modelAction class=\"java.util.Date\" hasDeclaredEffort=\"true\" newDeclaredEffort=\"3\">" +
					"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
					"      				</modelAction>" +
					"      				<modelAction class=\"java.lang.Integer\" newProgressDescription=\"d\">" +
					"         				<referenceId id=\"8DB3AC7B-E5A2-44A5-8507-EC0ACB389038\"/>" +
					"      				</modelAction>" +
					"   			</subActionList>" +
					"			</action>" +
					"		</userAction>" +
					"	</userActions>" +
					"</root>";

	@Test
	public void allInstancesOfSameMigrationIsEqual() {
		assertEquals(new Migration_2011_10_01(), new Migration_2011_10_01());
		assertEquals(new Migration2_2011_08_01(), new Migration2_2011_08_01());
	}

	@Test
	public void instancesOfDifferentMigrationsAreDifferentEvenWhenHasSameVersion() {
		assertFalse(new Migration2_2011_08_01().equals(new Migration_2011_10_01()));
		assertFalse(new Migration2_2011_10_01().equals(new Migration_2011_10_01()));
	}

	@Test
	public void theHashOfAMigrationShouldBeTheHashOfHisClassName() {
		assertEquals("br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations.Migration_2011_10_01".hashCode(),
				new Migration_2011_10_01().hashCode());
		assertEquals("br.com.oncast.ontrack.server.services.exportImport.xml.sample.migrations2.Migration2_2011_08_01".hashCode(),
				new Migration2_2011_08_01().hashCode());
		assertEquals(new Migration2_2011_08_01().hashCode(), new Migration2_2011_08_01().hashCode());
	}

	@Test
	public void migrationVersionShouldBeTheDateOnHisName() throws Exception {
		assertEquals("2011_10_01", new Migration_2011_10_01().getVersion());
		assertEquals("2011_08_01", new Migration2_2011_08_01().getVersion());
	}

	@Test
	public void migrationsIsOrderedByTheVersionOnHisName() throws Exception {
		assertTrue(new Migration2_2011_08_01().compareTo(new Migration_2011_10_01()) < 0);
		assertTrue(new Migration2_2011_11_10().compareTo(new Migration_2011_10_01()) > 0);
		assertTrue(new Migration2_2011_10_01().compareTo(new Migration_2011_10_01()) == 0);
	}

	@Test
	public void migrationShouldNotBeExecutedIfDocumentVersionIsGreaterThanMigrationVersion() throws Exception {
		final String migrationVersion = "1320175697998";
		final String documentVersion = "1320175697999";
		assertTrue(documentVersion.compareTo(migrationVersion) > 0);

		final Document document = DocumentHelper.parseText(testXML);
		final Migration spy = Mockito.spy(new Migration2_2011_08_01());
		Mockito.when(spy.getVersion()).thenReturn(migrationVersion);

		spy.apply(document);

		Mockito.verify(spy, Mockito.never()).execute();
	}

	@Test
	public void shouldBeAbleToAddAElementOnAGivenParent() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Element parent = document.getRootElement();
		final int previousNodeCount = parent.nodeCount();
		assertNull(parent.element("AnyElement"));
		final Element addedElement = new Migration2_2011_08_01().addElementWithClassAttribute(parent, "AnyElement", "java.lang.Object");
		final int currentNodeCount = parent.nodeCount();
		assertEquals(previousNodeCount + 1, currentNodeCount);
		final Element element = parent.element("AnyElement");
		assertNotNull(element);
		assertEquals(addedElement, element);
	}

	@Test
	public void addedElementShouldBeReturnedByTheMethod() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Element parent = document.getRootElement();
		final Element addedElement = new Migration2_2011_08_01().addElementWithClassAttribute(parent, "AnyElement", "java.lang.Object");
		assertNotNull(addedElement);
		assertEquals(addedElement, parent.element("AnyElement"));
	}

	@Test
	public void addedElementsShouldHaveAAttributeNamedClassAndHisValueShouldBeTheElementsJavaTypeName() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Element parent = document.getRootElement();
		final Element addedElement = new Migration2_2011_08_01().addElementWithClassAttribute(parent, "AnyElement", "java.lang.Object");
		assertEquals("AnyElement", addedElement.getName());
		assertEquals(1, addedElement.attributeCount());
		final Attribute classAttribute = addedElement.attribute(0);
		assertNotNull(classAttribute);
		assertEquals("class", classAttribute.getName());
		assertEquals("java.lang.Object", classAttribute.getValue());
	}

	@Test
	public void addListShouldAddJavaArrayListTypeElement() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Element parent = document.getRootElement();
		final Element addedList = new Migration2_2011_08_01().addListElementTo(parent, "users");
		assertEquals("users", addedList.getName());
		assertEquals("java.util.ArrayList", addedList.attributeValue("class"));
	}

	@Test
	public void getRootElementShouldReturnTheDocumentsRootElement() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Migration migration = new Migration2_2011_08_01();
		migration.apply(document);
		assertEquals("root", migration.getRootElement().getName());
		assertEquals("1320175697999", migration.getRootElement().attributeValue("version"));

	}

	@Test
	public void theDocumentGivenInApplyMethodShouldBeStored() throws Exception {
		final Document document = DocumentHelper.parseText(testXML);
		final Migration migration = new Migration2_2011_08_01();
		migration.apply(document);
		assertSame(document, migration.getDocument());
	}

}
