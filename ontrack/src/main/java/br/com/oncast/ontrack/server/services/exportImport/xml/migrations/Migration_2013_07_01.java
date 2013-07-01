package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Removed user's project creation and user invitation quota and adds isSuperUser
 * <li>Remove unused description field from DescriptionRemoveAction
 * </ul>
 * 
 */
public class Migration_2013_07_01 extends Migration {

	private static final String DESCRIPTION_REMOVE_ACTION = "br.com.oncast.ontrack.shared.model.action.DescriptionRemoveAction";

	@Override
	protected void execute() throws Exception {
		removeQuotasAndAddIsSuperUserToAllUsers();
		removeDescriptionFromDescriptionRemoveAction();
	}

	private void removeDescriptionFromDescriptionRemoveAction() {
		for (final Element action : getElementsWithClassAttribute(DESCRIPTION_REMOVE_ACTION)) {
			final Element description = action.element("description");
			action.remove(description);
		}
	}

	private void removeQuotasAndAddIsSuperUserToAllUsers() {
		for (final Element user : getElements("//userData")) {
			final Attribute invitation = user.attribute("projectInvitationQuota");
			final Attribute creation = user.attribute("projectCreationQuota");
			user.remove(creation);
			user.remove(invitation);
			user.addAttribute("superUser", hasAny(invitation, creation) ? "true" : "false");
		}
	}

	private boolean hasAny(final Attribute invitation, final Attribute creation) {
		return isNotZero(invitation) || isNotZero(creation);
	}

	private boolean isNotZero(final Attribute attribute) {
		return !"0".equals(attribute.getValue());
	}

}
