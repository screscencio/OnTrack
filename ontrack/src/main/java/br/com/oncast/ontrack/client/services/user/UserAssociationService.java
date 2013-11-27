package br.com.oncast.ontrack.client.services.user;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import java.util.List;

public interface UserAssociationService {

	List<UserRepresentation> getAssociatedUsers(Scope scope);

	boolean onAssociateUserRequest(Scope scope, UserRepresentation user);

	void onUserRemoveAssociationRequest(Scope scope, UserRepresentation user);

	boolean hasAssociatedUser(Scope scope, UserRepresentation user);
}
