package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

public interface UserAuthenticationCallback {

	void onUserAuthenticatedSuccessfully(User user);

	void onIncorrectCredentialsFailure();

	void onUnexpectedFailure(Throwable caught);

}
