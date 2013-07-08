package br.com.oncast.ontrack.shared.model.user;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

public class UserRepresentation implements HasUUID, Serializable, Comparable<UserRepresentation> {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private boolean valid = true;

	private boolean readOnly = false;

	private boolean canInvite = true;

	public UserRepresentation() {}

	public UserRepresentation(final UUID id) {
		this.setId(id);
	}

	public UserRepresentation(final UUID userId, final boolean canInvite, final boolean readOnly) {
		this.setId(userId);
		this.setCanInvite(canInvite);
		this.setReadOnly(readOnly);
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

	public void setId(final UUID id) {
		this.id = id;
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

	public void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean canInvite() {
		return canInvite;
	}

	public void setCanInvite(final boolean canInvite) {
		this.canInvite = canInvite;
	}
}
