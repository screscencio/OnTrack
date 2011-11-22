package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
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
