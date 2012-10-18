package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserSelectedScopeEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private UUID userId;
	private UUID scopeId;

	protected UserSelectedScopeEvent() {}

	public UserSelectedScopeEvent(final UUID userId, final UUID scopeId) {
		this.userId = userId;
		this.scopeId = scopeId;
	}

	public UUID getScopeId() {
		return scopeId;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}

}