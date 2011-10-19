package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@ConvertTo(UserEntity.class)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String email;
	private String password;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public boolean authenticate(final String password) {
		return this.password.equals(password);
	}
}
