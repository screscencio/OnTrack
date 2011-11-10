package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLUtils {

	public static Document read(final File file) throws DocumentException {
		return new SAXReader().read(file);
	}

	public static void print(final Document document) throws UnsupportedEncodingException, IOException {
		final OutputFormat format = OutputFormat.createPrettyPrint();
		new XMLWriter(System.out, format).write(document);
	}

	public static void write(final Document document, final File file) throws IOException {
		final XMLWriter writer = new XMLWriter(new FileWriter(file));
		writer.write(document);
		writer.close();
	}
}
