package br.com.oncast.ontrack.shared.model.user;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.UserEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "userData")
@ConvertTo(UserEntity.class)
public class User implements Serializable, Comparable<User>, HasUUID {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID id;

	@Attribute
	private String email;

	@Attribute(required = false)
	private String name;

	@Element(required = false)
	private UUID userPictureId;

	@Element
	private Profile globalProfile;

	User() {}

	public User(final UUID id, final String email, final Profile globalProfile) {
		this.id = id;
		this.email = email;
		this.globalProfile = globalProfile;
	}

	public User(final UUID id, final String email) {
		this(id, email, Profile.getDefaultProfile());
	}

	@Override
	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public boolean canManageProjects() {
		return getGlobalProfile().hasPermissionsOf(Profile.PROJECT_MANAGER);
	}

	public Profile getGlobalProfile() {
		return globalProfile;
	}

	public void setGlobalProfile(final Profile globalProfile) {
		this.globalProfile = globalProfile;
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
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
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
