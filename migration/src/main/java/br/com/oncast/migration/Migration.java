package br.com.oncast.migration;

import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.Element;

public abstract class Migration implements Comparable<Migration>{
	
	private static final String VERSION = "version";
	private static final String CLASS = "class";
	private static final char SEPARATOR = '_';

	public void apply(Document document) throws Exception {
		if (shouldBeMigrated(document)) execute(document);
	}
	
	public String getDateString() {
		String name = this.getClass().getSimpleName();
		return name.substring(name.indexOf(SEPARATOR) + 1);
	}
	
	protected abstract void execute(Document document) throws Exception;
	
	protected Element addList(Element parent, String name) {
		return add(parent, name, ArrayList.class);
	}
	
	protected Element add(Element parent, String name, Class<?> javaClass){
		Element element = parent.addElement(name);
		element.addAttribute(CLASS, javaClass.getName());
		return element;
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
		return getDateString();
	}

	public int compareTo(Migration o) {
		return this.getDateString().compareTo(o.getDateString());
	}

	private boolean shouldBeMigrated(Document document) {
		return getDateString().compareTo(document.getRootElement().attributeValue(VERSION)) > 0;
	}

}
