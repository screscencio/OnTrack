package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.user.User;

public class UserDataRequestResponse implements DispatchResponse {

	private List<User> users;

	protected UserDataRequestResponse() {}

	public UserDataRequestResponse(final List<User> users) {
		this.users = users;
	}

	public List<User> getUsers() {
		return users;
	}

}
