package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

public interface AuthenticationService {

	void authenticate(String login, String password, UserAuthenticationCallback callback);

	void logout(UserLogoutCallback dispatchCallback);

	void changePassword(String oldPassword, String newPassword, UserPasswordChangeCallback callback);

	void registerUserAuthenticationListener(UserAuthenticationListener listener);

	void unregisterUserAuthenticatedListener(UserAuthenticationListener listener);

	User getCurrentUser();

	void loadCurrentUserInformation(UserInformationLoadCallback userInformationLoadCallback);

	void registerAuthenticationExceptionGlobalHandler();

	boolean isUserAvailable();

	void onUserLogout();
}
