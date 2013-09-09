package br.com.oncast.ontrack.shared.model.user;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

public class UserRepresentation implements HasUUID, Serializable, Comparable<UserRepresentation> {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private boolean valid = true;

	private Profile projectProfile;

	public UserRepresentation() {}

	public UserRepresentation(final UUID id) {
		this(id, Profile.getDefaultProfile());
	}

	public UserRepresentation(final UUID userId, final Profile projectProfile) {
		this.id = userId;
		this.projectProfile = projectProfile;
	}

	public void setValid(final boolean isValid) {
		this.valid = isValid;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public UUID getId() {
		return id;
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
	public int compareTo(final UserRepresentation o) {
		return this.id.toString().compareTo(o.getId().toString());
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public void setProjectProfile(final Profile projectProfile) {
		this.projectProfile = projectProfile;
	}

	public boolean isReadOnly() {
		return getProjectProfile().isReadOnly();
	}

	public Profile getProjectProfile() {
		return projectProfile;
	}

	public boolean canInvitePeople() {
		return projectProfile.hasPermissionsOf(Profile.PEOPLE_MANAGER);
	}

}
