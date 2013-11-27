package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.user.User;

public class UserDataUpdateRequest implements DispatchRequest<UserDataUpdateRequestResponse> {

	private User user;

	public UserDataUpdateRequest() {}

	public UserDataUpdateRequest(final User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
