package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface UserInformationLoadCallback {

	public void onUserInformationLoaded(UUID currentUser, boolean isCurrentUserActivated);

	public void onUnexpectedFailure(final Throwable cause);
}
