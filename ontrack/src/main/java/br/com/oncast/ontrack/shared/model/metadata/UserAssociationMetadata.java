package br.com.oncast.ontrack.shared.model.metadata;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

public class UserAssociationMetadata implements Metadata, Serializable {

	private static final long serialVersionUID = 1L;

	private Scope scope;

	private UserRepresentation user;

	private UUID id;

	public UserAssociationMetadata() {}

	public UserAssociationMetadata(final UUID metadataId, final Scope scope, final UserRepresentation user) {
		this.id = metadataId;
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
	public HasMetadata getSubject() {
		return scope;
	}

	@Override
	public MetadataType getMetadataType() {
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

	public static MetadataType getType() {
		return MetadataType.USER;
	}

}
