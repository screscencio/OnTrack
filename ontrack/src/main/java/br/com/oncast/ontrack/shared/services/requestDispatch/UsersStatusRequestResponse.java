package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

public class UsersStatusRequestResponse implements DispatchResponse {

	private Set<String> activeUsers;
	private Set<String> onlineUsers;

	protected UsersStatusRequestResponse() {}

	public UsersStatusRequestResponse(final Set<String> activeUsers, final Set<String> onlineUsers) {
		this.activeUsers = activeUsers;
		this.onlineUsers = onlineUsers;
	}

	public Set<String> getActiveUsers() {
		return activeUsers;
	}

	public Set<String> getOnlineUsers() {
		return onlineUsers;
	}

}
