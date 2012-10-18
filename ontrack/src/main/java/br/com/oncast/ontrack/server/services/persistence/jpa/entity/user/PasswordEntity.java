package br.com.oncast.ontrack.server.services.persistence.jpa.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;

@Entity
@ConvertTo(Password.class)
public class PasswordEntity {

	@Id
	@GeneratedValue
	@ConversionAlias("id")
	private long id;

	private String passwordHash;
	private String passwordSalt;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "userId", unique = true, nullable = false)
	private String userId;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

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
}