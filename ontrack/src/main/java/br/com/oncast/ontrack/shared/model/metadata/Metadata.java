package br.com.oncast.ontrack.shared.model.metadata;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;

public interface Metadata extends HasUUID {

	HasMetadata getSubject();

	MetadataType getMetadataType();

}
