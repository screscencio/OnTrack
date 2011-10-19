package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.shared.model.user.User;

public interface AuthenticationService {

	boolean isUserLoggedIn();

	void authenticateUser(String login, String password, DispatchCallback<User> dispatchCallback);

	void logoutCurrentUser(DispatchCallback<Void> dispatchCallback);

	void changeCurrentUserPassword(String oldPassword, String newPassword, DispatchCallback<Void> dispatchCallback);
}
