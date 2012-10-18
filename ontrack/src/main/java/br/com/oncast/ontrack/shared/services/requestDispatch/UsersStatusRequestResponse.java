package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UsersStatusRequestResponse implements DispatchResponse {

	private Set<UUID> activeUsers;
	private Set<UUID> onlineUsers;

	protected UsersStatusRequestResponse() {}

	public UsersStatusRequestResponse(final Set<UUID> activeUsers, final Set<UUID> onlineUsers) {
		this.activeUsers = activeUsers;
		this.onlineUsers = onlineUsers;
	}

	public Set<UUID> getActiveUsers() {
		return activeUsers;
	}

	public Set<UUID> getOnlineUsers() {
		return onlineUsers;
	}

}
