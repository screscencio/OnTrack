package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserSelectedScopeEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private String userEmail;
	private UUID scopeId;

	protected UserSelectedScopeEvent() {}

	public UserSelectedScopeEvent(final String userEmail, final UUID scopeId) {
		this.userEmail = userEmail;
		this.scopeId = scopeId;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}

	public UUID getScopeId() {
		return scopeId;
	}

}