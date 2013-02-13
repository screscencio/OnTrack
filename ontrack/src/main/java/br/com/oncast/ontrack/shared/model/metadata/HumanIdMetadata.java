package br.com.oncast.ontrack.shared.model.metadata;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

public class HumanIdMetadata implements Metadata, Serializable {

	private static final long serialVersionUID = 1L;

	private UUID metadataId;

	private HasMetadata subject;

	private String humanId;

	public HumanIdMetadata() {}

	public HumanIdMetadata(final UUID metadataId, final HasMetadata subject, final String humanId) {
		this.metadataId = metadataId;
		this.subject = subject;
		this.humanId = humanId;
	}

	public String getHumanId() {
		return humanId;
	}

	@Override
	public UUID getId() {
		return metadataId;
	}

	@Override
	public HasMetadata getSubject() {
		return subject;
	}

	@Override
	public MetadataType getMetadataType() {
		return getType();
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	public static MetadataType getType() {
		return MetadataType.HUMAN_ID;
	}

}
