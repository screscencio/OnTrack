package br.com.oncast.ontrack.server.utils.mmConverter.abstractions;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MindNode {
	private final Document document;
	private final Element element;
	private List<MindNode> children;
	private List<Icon> icons;

	public MindNode(final Document document, final Element element) {
		this.document = document;
		this.element = element;
	}

	public String getText() {
		return element.getAttribute("TEXT");
	}

	public void setText(final String text) {
		element.setAttribute("TEXT", text);
	}

	public List<MindNode> getChildren() {
		if (children == null) interpret();
		return children;
	}

	public boolean hasIcon(final Icon icon) {
		if (icons == null) interpret();
		return icons.contains(icon);
	}

	public MindNode appendChild() {
		if (children == null) interpret();

		final Element child = document.createElement("node");
		element.appendChild(child);

		final String id = Long.toString(System.currentTimeMillis());
		child.setAttribute("CREATED", id);
		child.setAttribute("ID", "ID_" + id);
		child.setAttribute("MODIFIED", id);

		final MindNode mindNode = new MindNode(document, child);
		mindNode.setText("");
		children.add(mindNode);
		return mindNode;
	}

	public void addIcon(final Icon icon) {
		if (icons == null) interpret();

		final Element child = document.createElement("icon");
		element.appendChild(child);

		child.setAttribute("BUILTIN", icon.getFreemindCode());

		icons.add(icon);
	}

	private void interpret() {
		children = new ArrayList<MindNode>();
		icons = new ArrayList<Icon>();

		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			final Node n = element.getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			if ("node".equals(n.getNodeName())) children.add(new MindNode(document, (Element) n));
			if ("icon".equals(n.getNodeName())) icons.add(interpretIconNode(n));
		}
	}

	private Icon interpretIconNode(final Node n) {
		final String iconName;
		try {
			iconName = n.getAttributes().getNamedItem("BUILTIN").getNodeValue();
		}
		catch (final Exception e) {
			throw new RuntimeException("Unable to interpret icon node on FreeMind MindMap (unable to retrieve 'BUILTIN' attribute from icon node).", e);
		}
		for (final Icon i : Icon.values()) {
			if (i.getFreemindCode().equals(iconName)) return i;
		}

		throw new RuntimeException("Unable to interpret icon node on FreeMind MindMap (unknown 'BUILTIN' attribute: '" + iconName + "').");
	}
}
