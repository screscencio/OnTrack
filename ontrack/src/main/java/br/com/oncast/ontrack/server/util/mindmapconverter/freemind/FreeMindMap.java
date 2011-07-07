package br.com.oncast.ontrack.server.util.mindmapconverter.freemind;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class FreeMindMap {
	private final Document xml;

	private FreeMindMap(final InputStream stream) {
		final DOMParser domParser = new DOMParser();

		try {
			domParser.parse(new InputSource(stream));
			xml = domParser.getDocument();
		} catch (final Exception e) {
			throw new RuntimeException("Unable to parse mind map stream.", e);
		}
	}

	public static FreeMindMap open(final File mm) {
		try {
			return new FreeMindMap(new FileInputStream(mm));
		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Unable to open mind map.", e);
		}
	}

	public MindNode root() {
		final Element mapElement = xml.getDocumentElement();

		for (int i = 0; i < mapElement.getChildNodes().getLength(); i++) {
			final Node n = mapElement.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			if (!"node".equals(n.getNodeName())) continue;
			return new MindNode(xml, (Element) n);
		}

		throw new RuntimeException("Unable to open node.");
	}

	public void write(final OutputStream stream) {
		try {
			final Transformer t = TransformerFactory.newInstance().newTransformer();
			t.transform(new DOMSource(xml), new StreamResult(stream));
		} catch (final Exception e) {
			throw new RuntimeException("Unable to write FreeMind MindMap to XML output stream.", e);
		}
	}
}
