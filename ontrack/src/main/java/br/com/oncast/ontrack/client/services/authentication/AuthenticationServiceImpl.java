package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final AuthenticationRpcServiceAsync rpcServiceAsync = GWT.create(AuthenticationRpcService.class);

	@Override
	public void authenticate(final String login, final String password, final UserAuthenticationCallback callback) {
		rpcServiceAsync.autheticateUser(login, password, new AsyncCallback<User>() {

			@Override
			public void onSuccess(final User user) {
				callback.onUserAuthenticatedSuccessfully(user);
			}

			@Override
			public void onFailure(final Throwable caught) {
				// XXX Auth; For a secure environment, the user should not be informed about which was incorrect: the user or password.
				if (caught instanceof UserNotFoundException) {
					callback.onIncorrectUserEmail();
				}
				else if (caught instanceof IncorrectPasswordException) {
					callback.onIncorrectUserPasswordFailure();
				}
				else callback.onUnexpectedFailure(caught);
			}
		});
	}

	@Override
	public void logout(final UserLogoutCallback callback) {
		rpcServiceAsync.logoutUser(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(final Void result) {
				callback.onUserLogout();
			}

			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});

	}

	@Override
	public void changePassword(final String currentPassword, final String newPassword, final UserPasswordChangeCallback callback) {
		rpcServiceAsync.changeUserPassword(currentPassword, newPassword, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(final Void result) {
				callback.onUserPasswordChangedSuccessfully();
			}

			@Override
			public void onFailure(final Throwable caught) {
				if (caught instanceof IncorrectPasswordException) {
					callback.onIncorrectUserPasswordFailure();
				}
				else callback.onUnexpectedFailure(caught);
			}
		});
	}
}
