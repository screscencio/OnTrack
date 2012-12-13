package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserClosedProjectEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;

	private UUID userId;

	protected UserClosedProjectEvent() {}

	public UserClosedProjectEvent(final UUID userId) {
		this.userId = userId;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return "userId='" + userId + "'";
	}

}