package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserOnlineEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;

	private UUID userId;

	protected UserOnlineEvent() {}

	public UserOnlineEvent(final UUID userId) {
		this.userId = userId;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}
}