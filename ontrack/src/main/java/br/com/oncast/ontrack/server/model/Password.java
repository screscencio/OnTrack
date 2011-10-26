package br.com.oncast.ontrack.server.model;

import br.com.oncast.ontrack.server.services.persistence.jpa.PasswordHash;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.PasswordEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@ConvertTo(PasswordEntity.class)
public class Password {

	private long id;

	private long userId;
	private String passwordHash;
	private String passwordSalt;

	public Password() {}

	public void setId(final long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public void setPassword(final String password) {
		try {
			final PasswordHash hash = new PasswordHash(password);
			passwordHash = hash.getPasswordHash();
			passwordSalt = hash.getPasswordSalt();
		}
		catch (final Exception e) {
			throw new RuntimeException("Error encoding password for storage.", e);
		}
	}

	public boolean authenticate(final String password) {
		if (passwordHash == null && passwordSalt == null) {
			return password.isEmpty();
		}
		else {
			try {
				return new PasswordHash(password, passwordSalt).compareAgainst(passwordHash);
			}
			catch (final Exception e) {
				throw new RuntimeException("Error during authentication.", e);
			}
		}
	}
}