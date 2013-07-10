package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Adds canInvite and readOnly attribute to TeamInviteAction
 * </ul>
 * 
 */
public class Migration_2013_07_08 extends Migration {

	private static final String TEAM_INVITE = "br.com.oncast.ontrack.shared.model.action.TeamInviteAction";

	@Override
	protected void execute() throws Exception {
		final Map<String, String> isSuperUserMap = new HashMap<String, String>();
		for (final Element user : getElements("//userData")) {
			final String userId = user.element("id").attributeValue("id");
			isSuperUserMap.put(userId, user.attributeValue("superUser"));
		}

		for (final Element action : getElementsWithClassAttribute(TEAM_INVITE)) {
			final String userId = action.element("userId").attributeValue("id");
			action.addAttribute("canInvite", isSuperUserMap.get(userId));
			action.addAttribute("readOnly", "false");
		}
	}
}
