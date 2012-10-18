package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserOfflineEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;

	private UUID userId;

	protected UserOfflineEvent() {}

	public UserOfflineEvent(final UUID userId) {
		this.userId = userId;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}
}