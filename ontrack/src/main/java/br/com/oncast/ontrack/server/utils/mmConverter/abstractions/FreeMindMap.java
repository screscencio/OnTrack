package br.com.oncast.ontrack.server.utils.mmConverter.abstractions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class FreeMindMap {
	private Document xml;

	private FreeMindMap(final InputStream stream) {
		final DOMParser domParser = new DOMParser();

		try {
			domParser.parse(new InputSource(stream));
			xml = domParser.getDocument();
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to parse mind map stream.", e);
		}
	}

	private FreeMindMap() {
		createNewDocument();
		configure();
	}

	/**
	 * Create a new map, with basic configuration set.
	 */
	public static FreeMindMap createNewMap() {
		return new FreeMindMap();
	}

	public static FreeMindMap open(final File mm) {
		try {
			return new FreeMindMap(new FileInputStream(mm));
		}
		catch (final FileNotFoundException e) {
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
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "ASCII");
			t.transform(new DOMSource(xml), new StreamResult(stream));
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to write FreeMind MindMap to XML output stream.", e);
		}
	}

	private void createNewDocument() {
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactoryImpl.newInstance();
			final DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			xml = docBuilder.newDocument();
		}
		catch (final ParserConfigurationException e) {
			throw new RuntimeException("Unable to create a new mind map.", e);
		}
	}

	private void configure() {
		final Element container = xml.createElement("map");
		container.setAttribute("version", "0.9.0");
		xml.appendChild(container);

		addCommentTo(container);
		createRootNode(container);
	}

	private void addCommentTo(final Element container) {
		final Comment comment = xml.createComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");
		container.appendChild(comment);
	}

	private void createRootNode(final Element container) {
		final Element rootNode = xml.createElement("node");
		container.appendChild(rootNode);
	}
}
