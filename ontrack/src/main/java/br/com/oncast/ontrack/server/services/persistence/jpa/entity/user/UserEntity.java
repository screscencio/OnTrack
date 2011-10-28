package br.com.oncast.ontrack.server.services.persistence.jpa.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.user.User;

@Entity
@ConvertTo(User.class)
public class UserEntity {

	@Id
	@GeneratedValue
	@ConversionAlias("id")
	private long id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	public UserEntity() {}

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
}
