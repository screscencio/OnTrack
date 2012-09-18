package br.com.oncast.ontrack.client.ui.components.appmenu;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface PasswordChangeWidgetMessages extends Messages {

	@Description("empty password error message")
	@DefaultMessage("The new password cannot be empty.")
	String newPasswordCantBeEmpty();

	@Description("failed password confirmation error message")
	@DefaultMessage("You typed two different passwords.")
	String passwordConfirmationFailed();

	@Description("failed password min chars lenght requirement error message")
	@DefaultMessage("The new password must have at least 6 characters.")
	String passwordMinCharRequirementFailed();

	@Description("password change success message")
	@DefaultMessage("Password changed succesfully")
	String successfulChange();

	@Description("unexpected error")
	@DefaultMessage("Unexpected error.")
	String unexpectedError();

	@Description("incorrect original passowrd error message")
	@DefaultMessage("Incorrect old password.")
	String incorrectOldPassword();

}
