package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Replaces canInvite and readOnly attribute to Profile enumeration in TeamInviteAction
 * <li>Replaces superUser attribute to Profile enumeration in User
 * </ul>
 * 
 */
public class Migration_2013_09_03 extends Migration {

	private static final String GUEST = "GUEST";
	private static final String CONTRIBUTOR = "CONTRIBUTOR";
	private static final String PROJECT_MANAGER = "PROJECT_MANAGER";
	private static final String TEAM_INVITE_ACTION = "br.com.oncast.ontrack.shared.model.action.TeamInviteAction";

	@Override
	protected void execute() throws Exception {
		for (final Element user : getElements("//userData")) {
			final Boolean isSuperUser = removeAttribute(user, "superUser");
			user.addElement("globalProfile").setText(isSuperUser ? PROJECT_MANAGER : CONTRIBUTOR);
		}

		for (final Element action : getElementsWithClassAttribute(TEAM_INVITE_ACTION)) {
			final boolean canInvite = removeAttribute(action, "canInvite");
			final boolean readOnly = removeAttribute(action, "readOnly");
			action.addElement("projectProfile").setText(readOnly ? GUEST : canInvite ? PROJECT_MANAGER : CONTRIBUTOR);
		}
	}

	private boolean removeAttribute(final Element element, final String attributeName) {
		final Attribute attribute = element.attribute(attributeName);
		final Boolean value = Boolean.valueOf(attribute.getValue());
		element.remove(attribute);
		return value;
	}
}
