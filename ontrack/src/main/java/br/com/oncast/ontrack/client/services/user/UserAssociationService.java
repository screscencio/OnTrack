package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

public interface UserAssociationService {

	List<UserRepresentation> getAssociatedUsers(Scope scope);

	boolean onAssociateUserRequest(Scope scope, UserRepresentation user);

	void onUserRemoveAssociationRequest(Scope scope, UserRepresentation user);

	boolean hasAssociatedUser(Scope scope, UserRepresentation user);
}
