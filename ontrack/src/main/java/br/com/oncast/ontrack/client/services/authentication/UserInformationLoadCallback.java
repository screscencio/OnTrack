package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

public interface UserInformationLoadCallback {

	public void onUserInformationLoaded(User user);

	public void onUnexpectedFailure(final Throwable cause);
}
