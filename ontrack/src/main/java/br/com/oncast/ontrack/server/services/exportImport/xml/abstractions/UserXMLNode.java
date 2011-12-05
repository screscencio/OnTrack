package br.com.oncast.ontrack.server.services.exportImport.xml.abstractions;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.shared.model.user.User;

@Root(name = "user")
public class UserXMLNode {

	@Attribute
	private long id;

	@Attribute
	private String email;

	@Attribute(required = false)
	private String passwordHash;

	@Attribute(required = false)
	private String passwordSalt;

	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
	@SuppressWarnings("unused")
	private UserXMLNode() {}

	// FIXME Jaime / Matsumoto: Remove
	public UserXMLNode(final User user) {
		id = user.getId();
		email = user.getEmail();
	}

	public UserXMLNode(final long id, final String email) {
		this.id = id;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public User getUser() {
		return new User(email);
	}

	public boolean hasPassword() {
		return passwordHash != null && passwordSalt != null;
	}

	public Password getPassword() {
		if (!hasPassword()) return null;

		final Password password = new Password();
		password.setPasswordHash(passwordHash);
		password.setPasswordSalt(passwordSalt);
		return password;
	}

	public void setPassword(final Password password) {
		passwordHash = password.getPasswordHash();
		passwordSalt = password.getPasswordSalt();
	}

}
