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

	@Description("message shown when some error happens because of model inconsistency.")
	@DefaultMessage("It was not possible to update the view because an inconsistency with the model was detected.")
	String modelInconsistency();

	@Description("message shown when a user was not found.")
	@DefaultMessage("The user ''{0}'' was not found")
	String userNotFound(String userEmail);

	@Description("message shown when the user tries to show details and the given id was not found in the model.")
	@DefaultMessage("It was not possible to show details: The referenced entity does not exist")
	String errorShowingDetails();

	@Description("message shown when the project list is unavailable for some reason.")
	@DefaultMessage("Projects list unavailable. Verify your connection.")
	String projectListUnavailable();

	@Description("message shown when the user tries to show planning with a selected scope and the given id was not found.")
	@DefaultMessage("It was not possible to select the requested scope: It does not exist")
	String errorSelectingScope();

	@Description("message shown when the authorization for the current project is removed")
	@DefaultMessage("Your authorization for the project ''{0}'' were revogued")
	String authorizationRevogued(String projectName);

	@Description("message shown when tryind to reconnect to server")
	@DefaultMessage("Trying to reconnect...")
	String tryingToReconnect();

	@Description("message shown when reconnecting to server")
	@DefaultMessage("Engaging server!")
	String establishingConnection();

	@Description("message shown when resync was successful")
	@DefaultMessage("We are back!")
	String resyncSuccess();

	@Description("message shown when requesting new password for user")
	@DefaultMessage("Requesting new password for ''{0}''.")
	String requestingNewPassword(String username);

	@Description("message shown when new password request was successful")
	@DefaultMessage("An e-mail with a new passowrd was sent.")
	String passwordRequestSucessful();

	@Description("message shown when new password request failed because of bad username")
	@DefaultMessage("Invalid user... Nothing was done.")
	String passwordRequestFailedDueToBadUsername();

	@Description("message shown when new password request is done without username")
	@DefaultMessage("Please insert your username.")
	String passwordRequestNeedsUsernameInput();
}
