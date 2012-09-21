package br.com.oncast.ontrack.client.i18n;

public interface ClientErrorMessages extends BaseMessages {

	@Description("message shown when server push client receives an error")
	@DefaultMessage("No internet connection...")
	String noInternectConnection();

	@Description("message shown when incorrect credentials for login are provided")
	@DefaultMessage("Incorrect user or password.")
	String incorrectUserOrPassword();

	@Description("message shown when unexpected exceptions occur")
	@DefaultMessage("Unexpected error.")
	String unexpectedError();

	@Description("message shown when the action was not applied correctly on server")
	@DefaultMessage("The project is out of sync. Some changes may have been reverted.")
	String projectOutOfSync();

	@Description("message shown when there is no response from the server on action submition.")
	@DefaultMessage("Connection lost.")
	String connectionLost();

	@Description("message shown when the last applied action conflicts.")
	@DefaultMessage("Some of the lattest changes conflicted.")
	String someChangesConflicted();

}
