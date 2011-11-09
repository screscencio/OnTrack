package br.com.oncast.ontrack.server.util.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class MigrationTestUtils {

	public static Document readXMLFromFile(final String fileName) throws Exception {
		final SAXReader reader = new SAXReader();
		return reader.read("src/test/resources/migrations/" + fileName);
	}

	public static Document readXMLFromString(final String xml) throws Exception {
		return DocumentHelper.parseText(xml);
	}

	public static Element getElement(final Document document, final String xPath) {
		return (Element) document.selectObject(xPath);
	}

	public static boolean hasElement(final Document document, final String xPath) {
		return !(document.selectNodes(xPath)).isEmpty();
	}

	public static void assertElementAttributeValueIs(final Element element, final String attributeName, final String attributeValue) {
		assertEquals(attributeValue, element.attributeValue(attributeName));
	}

	public static void assertElementHasTheseAttributesAndNothingElse(final Element element, final String... attributeNames) {
		final List attributes = element.attributes();
		assertEquals(attributeNames.length, attributes.size());

		for (final String attributeName : attributeNames) {
			assertElementHasAttribute(element, attributeName);
		}
	}

	public static void assertElementHasAttribute(final Element element, final String attributeName) {
		assertNotNull("Element '" + element.getName() + "' doesn't have attribute named '" + attributeName, element.attribute(attributeName) + "'");
	}

	public static void assertElementDoesntExist(final Document document, final String xPath) {
		assertFalse(hasElement(document, xPath));
	}

	public static void assertElementExists(final Document document, final String xPath) {
		assertTrue("Element for xPath selector '" + xPath + "' doesn't exist", hasElement(document, xPath));
	}

	public static void assertElementTypeIs(final Document document, final Class<?> clazz, final String xPath) {
		final Object element = document.selectObject(xPath);
		assertTrue(element instanceof Element);

		final Attribute attribute = ((Element) element).attribute("class");
		assertNotNull(attribute);
		assertEquals(clazz.getName(), attribute.getValue());
	}
}
