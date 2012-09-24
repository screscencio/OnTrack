package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ApplicationMenuMessages extends BaseMessages {

	@Description("change password option text")
	@DefaultMessage("Change Password")
	String changePassword();

	@Description("logout with username text")
	@DefaultMessage("Logout, {0}")
	String logout(String username);

	@Description("logout without username text")
	@DefaultMessage("Logout")
	String logout();

	@Description("shown when logout fails")
	@DefaultMessage("Logout failed.")
	String logoutFailed();

}
