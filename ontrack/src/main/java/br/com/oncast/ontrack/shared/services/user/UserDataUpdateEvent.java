package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserDataUpdateEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private User user;

	protected UserDataUpdateEvent() {}

	public UserDataUpdateEvent(final User user) {
		this.user = user;
	}

	@Override
	public UUID getUserId() {
		return user.getId();
	}

	public User getUser() {
		return user;
	}

}
