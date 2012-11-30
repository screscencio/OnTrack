package br.com.oncast.ontrack.shared.model.tags;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserAssociationTag implements Tag, Serializable {

	private static final long serialVersionUID = 1L;

	private Scope scope;

	private UserRepresentation user;

	private UUID id;

	public UserAssociationTag() {}

	public UserAssociationTag(final UUID tagId, final Scope scope, final UserRepresentation user) {
		this.id = tagId;
		this.scope = scope;
		this.user = user;
	}

	public UserRepresentation getUser() {
		return user;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public HasTags getSubject() {
		return scope;
	}

	@Override
	public TagType getTagType() {
		return getType();
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
		final UserAssociationTag other = (UserAssociationTag) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	public static TagType getType() {
		return TagType.USER;
	}

}
