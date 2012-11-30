package br.com.oncast.ontrack.shared.model.user;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserRepresentation implements Serializable, Comparable<UserRepresentation> {

	private static final long serialVersionUID = 1L;

	private UUID id;

	public UserRepresentation() {}

	public UserRepresentation(final UUID id) {
		this.setId(id);
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
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
				if (other.getId() != null) return false;
			}
			else if (!id.equals(other.getId())) return false;
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
	public int compareTo(final UserRepresentation o) {
		return this.id.toString().compareTo(o.getId().toString());
	}
}