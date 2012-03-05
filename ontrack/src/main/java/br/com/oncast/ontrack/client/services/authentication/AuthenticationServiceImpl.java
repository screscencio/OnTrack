package br.com.oncast.ontrack.client.services.authentication;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final Set<UserAuthenticationListener> userAuthenticatedListeners;

	private final ApplicationPlaceController applicationPlaceController;

	private final DispatchService dispatchService;

	private User currentUser;

	public AuthenticationServiceImpl(final DispatchService dispatchService, final ApplicationPlaceController applicationPlaceController) {
		this.dispatchService = dispatchService;
		this.applicationPlaceController = applicationPlaceController;
		userAuthenticatedListeners = new HashSet<UserAuthenticationListener>();

		dispatchService.addFailureHandler(NotAuthenticatedException.class, new FailureHandler<NotAuthenticatedException>() {

			@Override
			public void handle(final NotAuthenticatedException caught) {
				applicationPlaceController.goTo(new LoginPlace(applicationPlaceController.getCurrentPlace()));
			}
		});
	}

	@Override
	public void authenticate(final String user, final String password, final UserAuthenticationCallback callback) {
		dispatchService.dispatch(new AuthenticationRequest(user, password), new DispatchCallback<AuthenticationResponse>() {

			@Override
			public void onSuccess(final AuthenticationResponse result) {
				currentUser = result.getUser();
				callback.onUserAuthenticatedSuccessfully(currentUser);
				notifyLoginToUserAuthenticationListeners();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onUnexpectedFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				if (caught instanceof InvalidAuthenticationCredentialsException) callback.onIncorrectCredentialsFailure();
				else callback.onUnexpectedFailure(caught);
			}
		});
	}

	@Override
	public void logout(final UserLogoutCallback callback) {
		dispatchService.dispatch(new DeAuthenticationRequest(), new DispatchCallback<VoidResult>() {

			@Override
			public void onSuccess(final VoidResult result) {
				onUserLogout();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				onUserLogout();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			private void onUserLogout() {
				callback.onUserLogout();
				notifyLogoutToUserAuthenticationListeners();
				applicationPlaceController.goTo(new LoginPlace());
			}
		});
	}

	@Override
	public void changePassword(final String currentPassword, final String newPassword, final UserPasswordChangeCallback callback) {
		dispatchService.dispatch(new ChangePasswordRequest(currentPassword, newPassword), new DispatchCallback<VoidResult>() {

			@Override
			public void onSuccess(final VoidResult result) {
				callback.onUserPasswordChangedSuccessfully();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onUnexpectedFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
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

	@Override
	public User getCurrentUser() {
		return currentUser;
	}
}
