package br.com.oncast.ontrack.client.i18n;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface ClientErrorMessages extends Messages {

	@Description("message shown when server push client receives an error")
	@DefaultMessage("No internet connection...")
	String noInternectConnection();

	@Description("message shown when incorrect credentials for login are provided")
	@DefaultMessage("Incorrect user or password.")
	String incorrectUserOrPassword();

	@Description("message shown when unexpected exceptions occur")
	@DefaultMessage("Unexpected error.")
	String unexpectedError();

}
