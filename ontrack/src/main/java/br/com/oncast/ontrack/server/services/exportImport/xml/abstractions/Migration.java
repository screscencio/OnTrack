package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public abstract class Migration implements Comparable<Migration>{
	
	private static final String VERSION = "version";
	private static final String CLASS = "class";
	private static final char SEPARATOR = '_';
	private Document document;
	private Element root;

	public void apply(Document document) throws Exception {
		if (shouldBeMigrated(document)) {
			this.document = document;
			root = null;
			execute();
		}
	}
	
	public String getVersion() {
		String name = this.getClass().getSimpleName();
		return name.substring(name.indexOf(SEPARATOR) + 1);
	}
	
	protected abstract void execute() throws Exception;
	
	protected Element getRootElement(){
		return root == null ? root = document.getRootElement() : root;
	}
	
	protected Element addListElementTo(Element parent, String listElementName) {
		return addElementOfType(parent, listElementName, ArrayList.class);
	}
	
	@SuppressWarnings("unchecked")
	protected List<Element> getAllElementsOfType(Class<?> javaClass) {
		return document.selectNodes("//*[@" + CLASS + "='" + javaClass.getName() + "']");
	}
	
	protected Element addElementOfType(Element parent, String name, Class<?> javaClass){
		Element element = parent.addElement(name);
		element.addAttribute(CLASS, javaClass.getName());
		return element;
	}
	
	protected Document getDocument() {
		return document;
	}
	
	@Override
	public boolean equals(Object obj) {
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

	public int compareTo(Migration o) {
		return this.getVersion().compareTo(o.getVersion());
	}

	private boolean shouldBeMigrated(Document document) {
		return getVersion().compareTo(document.getRootElement().attributeValue(VERSION)) > 0;
	}

}
