package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

interface AuthenticationRpcServiceAsync {

	void isCurrentUserAuthenticated(AsyncCallback<Boolean> callback);

	void logoutUser(AsyncCallback<Void> asyncCallback);

	void autheticateUser(String username, String password, AsyncCallback<User> callback);

	void changeUserPassword(String currentPassword, String newPassword, AsyncCallback<Void> callback);
}
