package br.com.oncast.ontrack.shared.model.metadata;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class MetadataFactory {

	public static UserAssociationMetadata createUserMetadata(final UUID metadataId, final Scope scope, final UserRepresentation user) {
		return new UserAssociationMetadata(metadataId, scope, user);
	}

	public static TagAssociationMetadata createTagMetadata(final UUID metadataId, final Scope scope, final Tag tag) {
		return new TagAssociationMetadata(metadataId, scope, tag);
	}

	public static HumanIdMetadata createHumanIdMetadata(final UUID metadataId, final Scope scope, final String humanId) {
		return new HumanIdMetadata(metadataId, scope, humanId);
	}

}
