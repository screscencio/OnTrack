package br.com.oncast.ontrack.shared.model.tags;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

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
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public static TagType getType() {
		return TagType.USER;
	}

}
