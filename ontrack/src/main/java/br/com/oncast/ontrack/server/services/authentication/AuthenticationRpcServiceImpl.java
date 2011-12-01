package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationRpcService;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AuthenticationRpcServiceImpl extends RemoteServiceServlet implements AuthenticationRpcService {

	private static final long serialVersionUID = 1L;

	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	@Override
	public User autheticateUser(final String username, final String password) throws UserNotFoundException, InvalidAuthenticationCredentialsException {
		return AUTHENTICATION_MANAGER.authenticate(username, password);
	}

	@Override
	public Boolean isCurrentUserAuthenticated() {
		return AUTHENTICATION_MANAGER.isUserAuthenticated();
	}

	@Override
	public void changeUserPassword(final String currentPassword, final String newPassword) throws InvalidAuthenticationCredentialsException {
		AUTHENTICATION_MANAGER.updateUserPassword(currentPassword, newPassword);
	}

	@Override
	public void logoutUser() {
		AUTHENTICATION_MANAGER.logout();
	}
}
