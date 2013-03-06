package br.com.oncast.ontrack.client.ui.places.login;

public interface ResetPasswordCallback {

	void onUserPasswordResetSuccessfully();

	void onUnexpectedFailure(Throwable caught);

	void onIncorrectCredentialsFailure();

}
