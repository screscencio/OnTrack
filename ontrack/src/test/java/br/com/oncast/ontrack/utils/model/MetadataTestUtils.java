package br.com.oncast.ontrack.utils.model;

import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataTestUtils {

	public static Metadata createMetadata() {
		return createMetadata(new UUID(), new UUID(), UserAssociationMetadata.getType());
	}

	public static Metadata createMetadata(final UUID subjectId, final UUID metadataId, final MetadataType type) {
		final Metadata metadata = mock(Metadata.class);
		final HasMetadata subject = mock(HasMetadata.class);
		when(subject.getId()).thenReturn(subjectId);

		when(metadata.getSubject()).thenReturn(subject);
		when(metadata.getId()).thenReturn(metadataId);
		when(metadata.getMetadataType()).thenReturn(type);

		return metadata;
	}

}
