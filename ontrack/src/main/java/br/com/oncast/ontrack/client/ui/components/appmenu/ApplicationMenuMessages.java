package br.com.oncast.ontrack.client.ui.components.appmenu;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface ApplicationMenuMessages extends Messages {

	@Description("change password option text")
	@DefaultMessage("Change Password")
	String changePassword();

	@Description("logout with username text")
	@DefaultMessage("Logout, {0}")
	String logout(String username);

	@Description("logout without username text")
	@DefaultMessage("Logout")
	String logout();

}
