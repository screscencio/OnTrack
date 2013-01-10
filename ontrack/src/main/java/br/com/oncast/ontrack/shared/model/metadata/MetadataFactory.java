package br.com.oncast.ontrack.shared.model.metadata;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class MetadataFactory {

	public static UserAssociationMetadata createUserMetadata(final UUID metadataId, final Scope scope, final UserRepresentation user) {
		return new UserAssociationMetadata(metadataId, scope, user);
	}

}
