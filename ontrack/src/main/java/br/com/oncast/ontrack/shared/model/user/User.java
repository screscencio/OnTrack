package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(User.class)
@Entity
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@IgnoredByDeepEquality
	private long id;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "projectInvitationQuota", nullable = false)
	private int projectInvitationQuota;

	@Column(name = "projectCreationQuota", nullable = false)
	private int projectCreationQuota;

	public User() {}

	public User(final String email, final int projectInvitationQuota, final int projectCreationQuota) {
		this.email = email;
		this.projectCreationQuota = projectCreationQuota;
		this.projectInvitationQuota = projectInvitationQuota;
	}

	public User(final String email) {
		this(email, 0, 0);
	}

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
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
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
