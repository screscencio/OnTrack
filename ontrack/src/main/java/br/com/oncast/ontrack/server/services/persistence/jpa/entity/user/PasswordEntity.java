package br.com.oncast.ontrack.server.services.persistence.jpa.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;

@Entity
@ConvertTo(Password.class)
public class PasswordEntity {

	@Id
	@GeneratedValue
	private long id;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "userId", nullable = false)
	private String userId;

	private String passwordHash;
	private String passwordSalt;

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(final String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}
}