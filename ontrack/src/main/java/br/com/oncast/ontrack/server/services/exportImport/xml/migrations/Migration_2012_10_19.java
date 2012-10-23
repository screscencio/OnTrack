package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

/**
 * Changes:
 * <ul>
 * <li>Replaces long id for UUID in User
 * <li>Updates TeamInviteAction for new User Model
 * <li>Updates UserAction for new User Model
 * <li>Updates ProjectAuthorization for new User Model
 * </ul>
 * 
 */
public class Migration_2012_10_19 extends Migration {

	private static final String USER_ID = "userId";
	private static final String ID = "id";
	private static final String INVITEE_EMAIL = "inviteeEmail";
	private static final String TEAM_INVITE_ACTION = "br.com.oncast.ontrack.shared.model.action.TeamInviteAction";
	private Map<String, String> idMap;
	private static final Map<String, String> emailMap;
	private static final Logger LOGGER = Logger.getLogger(Migration_2012_10_19.class);

	static {
		emailMap = new HashMap<String, String>();
		emailMap.put(DefaultAuthenticationCredentials.USER_EMAIL, DefaultAuthenticationCredentials.USER_ID.toStringRepresentation());
	}

	@Override
	protected void execute() throws Exception {
		idMap = new HashMap<String, String>();
		replaceLongForUUID();
		updateTeamInviteAction();
		updateUserActions();
		updateProjectAuthorizations();
	}

	private void updateProjectAuthorizations() {
		for (final Element auth : getElements("//projectAuthorization")) {
			final Attribute userId = auth.attribute(USER_ID);
			auth.remove(userId);
			auth.addElement(USER_ID).addAttribute(ID, idMap.get(userId.getValue()));
		}
	}

	private void updateUserActions() {
		for (final Element action : getElements("//userAction")) {
			final Attribute userId = action.attribute(USER_ID);
			action.remove(userId);

			action.addElement(USER_ID)
					.addAttribute(ID, idMap.get(userId.getValue()));
		}
	}

	private void updateTeamInviteAction() {
		for (final Element action : getElementsWithClassAttribute(TEAM_INVITE_ACTION)) {
			final Element element = action.element(INVITEE_EMAIL);
			action.remove(element);
			final String email = element.attributeValue(ID);

			action.addAttribute(INVITEE_EMAIL, email);
			action.addElement(USER_ID)
					.addAttribute(ID, emailMap.get(email));
		}
	}

	private void replaceLongForUUID() {
		for (final Element user : getElements("//user")) {
			final String email = user.attributeValue("email");
			if (!emailMap.containsKey(email)) emailMap.put(email, new UUID().toStringRepresentation());

			final String newId = emailMap.get(email);

			final Attribute id = user.attribute(ID);
			idMap.put(id.getValue(), newId);
			LOGGER.debug(email + ": " + id.getValue() + ": " + newId);

			user.remove(id);
			user.addElement(ID).addAttribute(ID, newId);
		}
	}
}
