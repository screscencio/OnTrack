package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Add Invitation and Project Creation limitations to User
 * <li>Add the user who requested the actions to UserAction.
 * </ul>
 * 
 */
public class Migration_2012_04_13 extends Migration {

	@Override
	protected void execute() throws Exception {
		updateUsers();
		addDefaultUserToUserActions();
	}

	private void addDefaultUserToUserActions() {
		for (final Element userAction : getElements("//userAction")) {
			userAction.addAttribute("userId", "1");
		}
	}

	private void updateUsers() {
		for (final Element user : getElements("//user")) {
			user.addAttribute("projectCreationQuota", "0");
			user.addAttribute("projectInvitationQuota", "0");
		}
	}

}
