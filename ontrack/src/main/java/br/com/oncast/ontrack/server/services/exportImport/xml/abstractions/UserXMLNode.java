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

	@Attribute
	private int projectCreationQuota;

	@Attribute
	private int projectInvitationQuota;

	@Attribute(required = false)
	private String passwordHash;

	@Attribute(required = false)
	private String passwordSalt;

	// IMPORTANT The Simple Framework needs a default constructor for instantiate classes.
	@SuppressWarnings("unused")
	private UserXMLNode() {}

	public UserXMLNode(final User user) {
		id = user.getId();
		email = user.getEmail();
		projectCreationQuota = user.getProjectCreationQuota();
		projectInvitationQuota = user.getProjectInvitationQuota();
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public int getProjectCreationQuota() {
		return projectCreationQuota;
	}

	public int getProjectInvitationQuota() {
		return projectInvitationQuota;
	}

	public User getUser() {
		final User user = new User(email);
		user.setProjectCreationQuota(projectCreationQuota);
		user.setProjectInvitationQuota(projectInvitationQuota);
		return user;
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
