package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

public interface UserAuthenticationCallback {

	void onUserAuthenticatedSuccessfully(User user);

	void onIncorrectUserEmail();

	void onIncorrectUserPasswordFailure();

	void onUnexpectedFailure(Throwable caught);

}
