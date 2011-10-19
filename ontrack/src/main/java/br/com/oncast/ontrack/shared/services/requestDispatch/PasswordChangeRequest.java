package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.io.Serializable;

public class PasswordChangeRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String oldPassword;
	private String newPassword;

	private String email;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public PasswordChangeRequest() {}

	public PasswordChangeRequest(final String email, final String oldPassword, final String newPassword) {
		this.email = email;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getEmail() {
		return email;
	}

}
