package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.DispatchResponseObjectContainer;
import br.com.oncast.ontrack.shared.model.user.User;

public class AuthenticationRequest implements DispatchRequest<DispatchResponseObjectContainer<User>> {

	private String email;
	private String password;

	protected AuthenticationRequest() {}

	public AuthenticationRequest(final String email, final String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
