package br.com.oncast.ontrack.shared.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class UserInformationChangeEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private boolean isSuperUser;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected UserInformationChangeEvent() {}

	public UserInformationChangeEvent(final User user) {
		id = user.getId();
		isSuperUser = user.isSuperUser();
	}

	public UUID getUserId() {
		return id;
	}

	public boolean isSuperUser() {
		return isSuperUser;
	}
}
