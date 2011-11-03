package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;

@ConvertTo(UserEntity.class)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Attribute
	private long id;

	@Attribute
	private String email;

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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final User other = (User) obj;
		if (email == null) {
			if (other.email != null) return false;
		}
		else if (!email.equals(other.email)) return false;
		if (id != other.id) return false;
		return true;
	}
}
