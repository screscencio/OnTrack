package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface MembersWidgetMessages extends BaseMessages {

	@Description("inivitation quota information message")
	@DefaultMessage("You have ''{0}'' invitations left.")
	String inivitationQuota(int invitationQuota);

	@Description("user invited with success message")
	@DefaultMessage("''{0}'' was invited!")
	String userInvited(String mail);

	@Description("user already invited error message")
	@DefaultMessage("''{0}'' already has been invited")
	String userAlreadyInvited(String mail);

}
