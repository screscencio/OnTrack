package br.com.oncast.ontrack.shared.model.tags;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class TagFactory {

	public static UserAssociationTag createUserTag(final UUID tagId, final Scope scope, final UserRepresentation user) {
		return new UserAssociationTag(tagId, scope, user);
	}

}
