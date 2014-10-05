package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.model.user.User;

public class CurrentUserInformationResponse implements DispatchResponse {

	private User user;

	private boolean isCurrentUserActivated;

	protected CurrentUserInformationResponse() {}

	public CurrentUserInformationResponse(final User user, final boolean isCurrentUserActivated) {
		this.user = user;
		this.isCurrentUserActivated = isCurrentUserActivated;
	}

	public User getUser() {
		return user;
	}

	public boolean isCurrentUserActivated() {
		return isCurrentUserActivated;
	}
}
