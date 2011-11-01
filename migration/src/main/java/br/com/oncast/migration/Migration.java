package br.com.oncast.migration;

import org.dom4j.Document;

public abstract class Migration {
	
	abstract void execute(Document document);
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.getClass().getSimpleName().equals(this.getClass().getSimpleName()); 
	}
}
