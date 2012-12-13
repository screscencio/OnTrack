package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface UserAuthenticationCallback {

	void onUserAuthenticatedSuccessfully(String username, UUID currentUser);

	void onIncorrectCredentialsFailure();

	void onUnexpectedFailure(Throwable caught);

}
