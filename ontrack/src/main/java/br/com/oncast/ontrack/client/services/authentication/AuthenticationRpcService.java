package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("authenticationService")
public interface AuthenticationRpcService extends RemoteService {

	User autheticateUser(final String username, final String password) throws UserNotFoundException, IncorrectPasswordException;

	Boolean isCurrentUserAuthenticated();

	void changeUserPassword(final String currentPassword, final String newPassword) throws IncorrectPasswordException;

	void logoutUser();
}
