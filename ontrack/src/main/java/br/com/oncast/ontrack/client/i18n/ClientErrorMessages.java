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

	@Description("message shown when the invited user accepts the invitation.")
	@DefaultMessage("The User ''{0}'' accepted the invitaton for this project")
	String acceptedInvitation(String userEmail);

	@Description("message shown when the user tries to show details and the given id was not found in the model.")
	@DefaultMessage("It was not possible to show details: The referenced entity does not exist")
	String errorShowingDetails();

	@Description("message shown when the project list is unavailable for some reason.")
	@DefaultMessage("Projects list unavailable. Verify your connection.")
	String projectListUnavailable();

	@Description("message shown when the user tries to show planning with a selected scope and the given id was not found.")
	@DefaultMessage("It was not possible to select the requested scope: It does not exist")
	String errorSelectingScope();

}
