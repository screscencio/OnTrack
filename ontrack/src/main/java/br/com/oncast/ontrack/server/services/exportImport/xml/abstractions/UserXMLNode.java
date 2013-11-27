package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.shared.model.user.User;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "user")
public class UserXMLNode {

	@Element
	private User userData;

	@Attribute(required = false)
	private String passwordHash;

	@Attribute(required = false)
	private String passwordSalt;

	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
	@SuppressWarnings("unused")
	private UserXMLNode() {}

	public UserXMLNode(final User user) {
		setUserData(user);
	}

	public User getUser() {
		return userData;
	}

	public boolean hasPassword() {
		return passwordHash != null && passwordSalt != null;
	}

	public Password getPassword() {
		if (!hasPassword()) return null;

		final Password password = new Password();
		password.setUserId(userData.getId());
		password.setPasswordHash(passwordHash);
		password.setPasswordSalt(passwordSalt);
		return password;
	}

	public void setPassword(final Password password) {
		passwordHash = password.getPasswordHash();
		passwordSalt = password.getPasswordSalt();
	}

	public User getUserData() {
		return userData;
	}

	public void setUserData(final User userData) {
		this.userData = userData;
	}
}
