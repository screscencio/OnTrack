package br.com.oncast.ontrack.client.ui.components.user;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface UserInformationCardMessages extends BaseMessages {

	@DefaultMessage("Name change has been changed")
	@Description("user name change success")
	String userNameChangeSuccess();

	@DefaultMessage("Name change was not changed")
	@Description("user name change failure")
	String userNameChangeFailure();

}
