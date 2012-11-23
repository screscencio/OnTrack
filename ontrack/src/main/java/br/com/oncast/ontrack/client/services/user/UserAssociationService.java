package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;

public interface UserAssociationService {

	List<User> getAssociatedUsers(Scope scope);

	boolean onAssociateUserRequest(Scope scope, User user);

	void onUserRemoveAssociationRequest(Scope scope, User user);

	boolean hasAssociatedUser(Scope scope, User user);
}
