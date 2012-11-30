package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Root(name = "userData")
@ConvertTo(UserEntity.class)
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID id;

	@Attribute
	private String email;

	@Attribute(required = false)
	private String name;

	@Element(required = false)
	private UUID userPictureId;

	@Attribute
	private int projectInvitationQuota;

	@Attribute
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

	public String getName() {
		return (name == null || name.isEmpty()) ? extractNameFromEmail() : name;
	}

	private String extractNameFromEmail() {
		return email.replaceAll("@.*$", "").replaceAll("\\.+", " ").trim();
	}

	public void setName(final String name) {
		this.name = name;
	}

	public UUID getUserPictureId() {
		return userPictureId;
	}

	public void setUserPictureId(final UUID userPictureId) {
		this.userPictureId = userPictureId;
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
		if (obj.getClass() == User.class) {
			final User other = (User) obj;
			if (id == null) {
				if (other.id != null) return false;
			}
			else if (!id.equals(other.id)) return false;
			return true;
		}
		else if (obj.getClass() == UserRepresentation.class) {
			final UserRepresentation other = (UserRepresentation) obj;
			if (id == null) {
				if (other.getId() != null) return false;
			}
			else if (!id.equals(other.getId())) return false;
			return true;
		}

		return false;
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
