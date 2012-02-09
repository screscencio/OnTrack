package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import static java.util.regex.Pattern.quote;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class Migration implements Comparable<Migration> {

	private static final String VERSION = "version";
	private static final String CLASS = "class";
	private static final char SEPARATOR = '_';
	private Document document;
	private Element root;

	public void apply(final Document document) throws Exception {
		if (shouldBeMigrated(document)) {
			this.document = document;
			root = null;
			execute();
		}
	}

	public String getVersion() {
		final String name = this.getClass().getSimpleName();
		return name.substring(name.indexOf(SEPARATOR) + 1);
	}

	protected abstract void execute() throws Exception;

	protected Element getRootElement() {
		return root == null ? root = document.getRootElement() : root;
	}

	@SuppressWarnings("unchecked")
	protected List<Element> getElements(final String xPath) {
		return getDocument().selectNodes(xPath);
	}

	protected Element addListElementTo(final Element parent, final String listElementName) {
		return addElementWithClassAttribute(parent, listElementName, "java.util.ArrayList");
	}

	@SuppressWarnings("unchecked")
	protected List<Element> getElementsWithClassAttribute(final String className) {
		return getDocument().selectNodes("//*[@" + CLASS + "='" + className + "']");
	}

	protected Element addElementWithName(final Element parent, final String name) {
		final Element element = parent.addElement(name);
		return element;
	}

	protected Element addElementWithClassAttribute(final Element parent, final String name, final String className) {
		final Element element = parent.addElement(name);
		element.addAttribute(CLASS, className);
		return element;
	}

	protected Document getDocument() {
		return document;
	}

	protected void renamePackage(final String originalPackage, final String targetPackage) {
		final List<Element> actions = getElements("//*[@" + CLASS + "]");
		for (final Element a : actions) {
			final Attribute att = a.attribute(CLASS);
			if (!att.getValue().startsWith(originalPackage)) continue;
			att.setValue(att.getValue().replaceAll(quote(originalPackage), targetPackage));
		}
	}

	protected void renameClass(final String classPackage, final String originalClassName, final String targetClassName) {
		final String originalClass = classPackage + "." + originalClassName;
		final String targetClass = classPackage + "." + targetClassName;

		final List<Element> actions = getElementsWithClassAttribute(originalClass);
		for (final Element a : actions) {
			final Attribute att = a.attribute(CLASS);
			if (!att.getValue().equals(originalClass)) continue;
			att.setValue(targetClass);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) return false;
		return obj.getClass() == this.getClass();
	}

	@Override
	public int hashCode() {
		return this.getClass().getName().hashCode();
	}

	@Override
	public String toString() {
		return getVersion();
	}

	@Override
	public int compareTo(final Migration o) {
		return this.getVersion().compareTo(o.getVersion());
	}

	private boolean shouldBeMigrated(final Document document) {
		return getVersion().compareTo(document.getRootElement().attributeValue(VERSION)) > 0;
	}
}
