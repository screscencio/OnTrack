package br.com.oncast.ontrack.server.services.exportImport.xml.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.migration.Migration;

// FIXME implement
public class Migration_2011_11_01_Test {

	private Document sourceDocument;
	private Migration migration;

	@Before
	public void loadXML() throws Exception {
		sourceDocument = read("ontrack_2011_10_01.xml");
	}

	@Before
	public void setUp() throws DocumentException {
		migration = new Migration_2011_11_01();
	}

	@Test
	public void shouldAddUsersListToRootElement() throws Exception {
		migration.apply(sourceDocument);
		assertElementTypeAndExistence(ArrayList.class, sourceDocument.selectObject("//users"));
	}

	private void assertElementTypeAndExistence(final Class<?> clazz, final Object element) {
		assertTrue(element instanceof Element);
		final Attribute attribute = ((Element) element).attribute("class");
		assertNotNull(attribute);
		assertEquals(clazz.getName(), attribute.getValue());
	}

	private Document read(final String fileName) throws Exception {
		final SAXReader reader = new SAXReader();
		return reader.read("src/test/resources/migrations/" + fileName);
	}
}
