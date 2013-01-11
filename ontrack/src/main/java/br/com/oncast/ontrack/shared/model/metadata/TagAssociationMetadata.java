package br.com.oncast.ontrack.shared.model.metadata;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

public class TagAssociationMetadata implements Metadata, Serializable {

	private static final long serialVersionUID = 1L;

	private Scope scope;

	private UUID id;

	private Tag tag;

	public TagAssociationMetadata() {}

	public TagAssociationMetadata(final UUID metadataId, final Scope scope, final Tag tag) {
		this.id = metadataId;
		this.scope = scope;
		this.tag = tag;
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
		return MetadataType.TAG;
	}

	public Tag getTag() {
		return tag;
	}

}
