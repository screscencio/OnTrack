package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

public class ActiveUsersRequestResponse implements DispatchResponse {

	private Set<String> activeUsers;

	protected ActiveUsersRequestResponse() {}

	public ActiveUsersRequestResponse(final Set<String> activeUsers) {
		this.activeUsers = activeUsers;
	}

	public Set<String> getActiveUsers() {
		return activeUsers;
	}

}
