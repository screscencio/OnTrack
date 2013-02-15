package org.dom4j.tree;

import org.dom4j.Element;

public class DOMTreeHelper {

	public static void insertAfter(final Element reference, final Element newAction) {
		final DefaultElement parent = (DefaultElement) reference.getParent();

		parent.addNode(parent.indexOf(reference) + 1, newAction);
	}

}
