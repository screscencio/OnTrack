package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(UserEntity.class)
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String email;

	private int projectInvitationQuota;

	private int projectCreationQuota;

	User() {}

	public User(final UUID id, final String email, final int projectInvitationQuota, final int projectCreationQuota) {
		this.id = id;
		this.email = email;
		this.projectCreationQuota = projectCreationQuota;
		this.projectInvitationQuota = projectInvitationQuota;
	}

	public User(final UUID id, final String email) {
		this(id, email, 0, 0);
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public int getProjectInvitationQuota() {
		return projectInvitationQuota;
	}

	public void setProjectInvitationQuota(final int projectInvitationQuota) {
		this.projectInvitationQuota = projectInvitationQuota;
	}

	public int getProjectCreationQuota() {
		return projectCreationQuota;
	}

	public void setProjectCreationQuota(final int projectCreationQuota) {
		this.projectCreationQuota = projectCreationQuota;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final User other = (User) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return email;
	}

	@Override
	public int compareTo(final User o) {
		return email.compareTo(o.getEmail());
	}

}
