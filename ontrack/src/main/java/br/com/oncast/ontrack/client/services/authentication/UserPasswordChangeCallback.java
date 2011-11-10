package br.com.oncast.ontrack.client.services.authentication;

public interface UserPasswordChangeCallback {

	void onUserPasswordChangedSuccessfully();

	void onIncorrectUserPasswordFailure();

	void onUnexpectedFailure(Throwable caught);
}
