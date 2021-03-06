package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Adds uniqueId to all user actions
 * <li>Renames timestamp to executionTimestamp
 * <li>Adds receiptTimestamp as beeing same date as executionTimestamp
 * </ul>
 * 
 */
public class Migration_2013_11_25 extends Migration {
	@Override
	protected void execute() throws Exception {
		for (final Element ua : getElements("//userAction")) {
			final String uniqueId = new UUID().toString();
			ua.addElement("uniqueId").addAttribute("id", uniqueId);
			final Attribute timestamp = ua.attribute("timestamp");
			ua.remove(timestamp);
			ua.addAttribute("executionTimestamp", timestamp.getValue());
			ua.addAttribute("receiptTimestamp", timestamp.getValue());
		}
	}
}
