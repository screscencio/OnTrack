package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

public class AuthenticationRequest implements DispatchRequest<AuthenticationResponse> {

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
