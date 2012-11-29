package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

/**
 * Changes:
 * <ul>
 * <li>Adds more data to users
 * <li>Replaces email to id in Notifications
 * <li>Removes email from TeamInviteAction
 * </ul>
 * 
 */
public class Migration_2012_11_29 extends Migration {

	private static final String USER_ID = "userId";
	private static final String ID = "id";
	private static final String TEAM_INVITE_ACTION = "br.com.oncast.ontrack.shared.model.action.TeamInviteAction";
	private Map<String, String> emailMap;

	@Override
	protected void execute() throws Exception {
		emailMap = new HashMap<String, String>();
		changeUserData();
		replaceAuthorMailInNotifications();
		replaceUserMailInNotificationRecipients();
		removeEmailFromTeamInviteAction();
	}

	private void changeUserData() {
		for (final Element user : getElements("//users/user")) {
			final Attribute email = user.attribute("email");
			final Attribute projectInvitationQuota = user.attribute("projectInvitationQuota");
			final Attribute projectCreationQuota = user.attribute("projectCreationQuota");
			final Element id = user.element(ID);

			user.remove(email);
			user.remove(projectInvitationQuota);
			user.remove(projectCreationQuota);
			user.remove(id);

			final Element data = user.addElement("userData");
			data.add(email);
			data.add(projectInvitationQuota);
			data.add(projectCreationQuota);
			data.add(id);

			emailMap.put(email.getValue(), id.attributeValue(ID));
		}
	}

	private void replaceAuthorMailInNotifications() {
		for (final Element notification : getElements("//notification")) {
			final Attribute authorMail = notification.attribute("authorMail");
			notification.remove(authorMail);
			notification.addElement("authorId")
					.addAttribute(ID, emailMap.get(authorMail.getValue()));
		}
	}

	private void replaceUserMailInNotificationRecipients() {
		for (final Element recipient : getElements("//notificationRecipient")) {
			final Element user = recipient.element("user");
			recipient.remove(user);
			recipient.addElement(USER_ID)
					.addAttribute(ID, emailMap.get(user.getText()));
		}
	}

	private void removeEmailFromTeamInviteAction() {
		for (final Element action : getElementsWithClassAttribute(TEAM_INVITE_ACTION)) {
			action.remove(action.attribute("inviteeEmail"));
		}
	}

}
