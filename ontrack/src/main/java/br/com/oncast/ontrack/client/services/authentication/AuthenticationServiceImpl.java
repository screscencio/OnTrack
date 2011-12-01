package br.com.oncast.ontrack.client.services.authentication;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final AuthenticationRpcServiceAsync rpcServiceAsync = GWT.create(AuthenticationRpcService.class);

	private final Set<UserAuthenticationListener> userAuthenticatedListeners;

	private final ApplicationPlaceController applicationPlaceController;

	public AuthenticationServiceImpl(final DispatchService dispatchService, final ApplicationPlaceController applicationPlaceController) {
		this.applicationPlaceController = applicationPlaceController;

		dispatchService.addFailureHandler(NotAuthenticatedException.class, new FailureHandler<NotAuthenticatedException>() {

			@Override
			public void handle(final NotAuthenticatedException caught) {
				applicationPlaceController.goTo(new LoginPlace(applicationPlaceController.getCurrentPlace()));
			}
		});
		userAuthenticatedListeners = new HashSet<UserAuthenticationListener>();
	}

	@Override
	public void authenticate(final String user, final String password, final UserAuthenticationCallback callback) {
		rpcServiceAsync.autheticateUser(user, password, new AsyncCallback<User>() {

			@Override
			public void onSuccess(final User user) {
				callback.onUserAuthenticatedSuccessfully(user);
				notifyLoginToUserAuthenticationListeners();
			}

			@Override
			public void onFailure(final Throwable caught) {
				if (caught instanceof InvalidAuthenticationCredentialsException) callback.onIncorrectCredentialsFailure();
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
				notifyLogoutToUserAuthenticationListeners();
				applicationPlaceController.goTo(new LoginPlace());
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
				if (caught instanceof InvalidAuthenticationCredentialsException) {
					callback.onIncorrectUserPasswordFailure();
				}
				else callback.onUnexpectedFailure(caught);
			}
		});
	}

	@Override
	public void registerUserAuthenticationListener(final UserAuthenticationListener listener) {
		userAuthenticatedListeners.add(listener);
	}

	@Override
	public void unregisterUserAuthenticatedListener(final UserAuthenticationListener listener) {
		userAuthenticatedListeners.remove(listener);
	}

	private void notifyLoginToUserAuthenticationListeners() {
		for (final UserAuthenticationListener listener : userAuthenticatedListeners) {
			listener.onUserLoggedIn();
		}
	}

	private void notifyLogoutToUserAuthenticationListeners() {
		for (final UserAuthenticationListener listener : userAuthenticatedListeners) {
			listener.onUserLoggedOut();
		}
	}
}
