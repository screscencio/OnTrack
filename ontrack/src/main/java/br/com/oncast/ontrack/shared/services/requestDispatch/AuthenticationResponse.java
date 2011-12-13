package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.user.User;

public class AuthenticationResponse implements DispatchResponse {

	private User user;

	protected AuthenticationResponse() {}

	public AuthenticationResponse(final User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
