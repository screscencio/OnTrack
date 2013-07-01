package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface MembersWidgetMessages extends BaseMessages {

	@Description("user invited with success message")
	@DefaultMessage("''{0}'' was invited!")
	String userInvited(String mail);

	@Description("user already invited error message")
	@DefaultMessage("''{0}'' already has been invited")
	String userAlreadyInvited(String mail);

	@Description("shown to indicate that the invitaion is beeing processed")
	@DefaultMessage("Processing your invitation...")
	String processingYourInvitation();

	@Description("shown to indicate that the invitator does not have permission to invite other people")
	@DefaultMessage("You don''t have permission to invite people")
	String permissionDenied();

}
