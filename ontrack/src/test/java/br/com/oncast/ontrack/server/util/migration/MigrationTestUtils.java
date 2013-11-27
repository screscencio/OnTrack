package br.com.oncast.ontrack.server.util.migration;

import java.io.IOException;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.simpleframework.xml.Root;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("rawtypes")
public class MigrationTestUtils {

	public static final String MIGRATION_XML_FILES_PACKAGE = "src/test/resources/migrations/";

	public static Document readXMLFromFile(final String fileName) throws Exception {
		final SAXReader reader = new SAXReader();
		return reader.read(MIGRATION_XML_FILES_PACKAGE + fileName);
	}

	public static Document readXMLFromString(final String xml) throws Exception {
		return DocumentHelper.parseText(xml);
	}

	public static Element getElement(final Document document, final String xPath) {
		return (Element) document.selectObject(xPath);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getElements(final Document document, final String xPath) {
		return document.selectNodes(xPath);
	}

	public static boolean hasXPathObject(final Document document, final String xPath) {
		return !(document.selectNodes(xPath)).isEmpty();
	}

	public static void assertElementAttributeValueIs(final Element element, final String attributeName, final String attributeValue) {
		assertEquals(attributeValue, element.attributeValue(attributeName));
	}

	public static void assertElementHasTheseAttributesAndNothingElse(final Element element, final String... attributeNames) {
		final List attributes = element.attributes();
		assertEquals("Elements doesn't have the same number of attributes.", attributeNames.length, attributes.size());

		for (final String attributeName : attributeNames) {
			assertElementHasAttribute(element, attributeName);
		}
	}

	public static void assertElementHasAttribute(final Element element, final String attributeName) {
		assertNotNull("Element '" + element.getName() + "' doesn't have attribute named '" + attributeName, element.attribute(attributeName) + "'");
	}

	public static void assertElementDoesntExist(final Document document, final String xPath) {
		assertFalse("Element for xPath selector '" + xPath + "' exists", hasXPathObject(document, xPath));
	}

	public static void assertElementExists(final Document document, final String xPath) {
		assertTrue("Element for xPath selector '" + xPath + "' doesn't exist", hasXPathObject(document, xPath));
	}

	public static void assertAttributeDoesntExist(final Document document, final String xPath) {
		assertFalse("Attribute for xPath selector '" + xPath + "' exists", hasXPathObject(document, xPath));
	}

	public static void assertAttributeExists(final Document document, final String xPath) {
		assertTrue("Attribute for xPath selector '" + xPath + "' doesn't exist", hasXPathObject(document, xPath));
	}

	public static void assertElementsType(final Document document, final Class<?> clazz, final String xPath) {
		final List nodes = document.selectNodes(xPath);
		for (final Object object : nodes) {
			assertTrue(object instanceof Element);

			final Element element = (Element) object;
			final Attribute attribute = element.attribute("class");
			if (attribute != null) assertEquals(clazz.getName(), attribute.getValue());
			else assertEquals(getElementName(clazz), element.getName());
		}
	}

	public static void print(final Document document) throws IOException {
		final OutputFormat format = OutputFormat.createPrettyPrint();
		final XMLWriter writer = new XMLWriter(System.out, format);
		writer.write(document);
	}

	private static String getElementName(final Class<?> javaClass) {
		final Root rootAnnotation = javaClass.getAnnotation(Root.class);
		if (rootAnnotation == null || rootAnnotation.name().isEmpty()) return lowerCaseTheCharAt(javaClass.getSimpleName(), 0);
		return rootAnnotation.name();
	}

	private static String lowerCaseTheCharAt(final String name, final int index) {
		return name.substring(index, index + 1).toLowerCase() + name.substring(index + 1);
	}

}
