package br.com.oncast.ontrack.client.services.authentication;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.login.ResetPasswordCallback;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.authentication.UserInformationChangeEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordResetRequest;

import com.google.gwt.place.shared.Place;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final Set<UserAuthenticationListener> userAuthenticatedListeners;

	private final ApplicationPlaceController applicationPlaceController;

	private final DispatchService dispatchService;

	private UUID currentUserId;

	private boolean isSuperUser;

	public AuthenticationServiceImpl(final DispatchService dispatchService, final ApplicationPlaceController applicationPlaceController, final ServerPushClientService serverPushClientService) {
		this.dispatchService = dispatchService;
		this.applicationPlaceController = applicationPlaceController;
		userAuthenticatedListeners = new HashSet<UserAuthenticationListener>();

		serverPushClientService.registerServerEventHandler(UserInformationChangeEvent.class, new ServerPushEventHandler<UserInformationChangeEvent>() {
			@Override
			public void onEvent(final UserInformationChangeEvent event) {
				processUserInformationUpdate(event);
			}
		});
	}

	@Override
	public void registerAuthenticationExceptionGlobalHandler() {
		dispatchService.addFailureHandler(NotAuthenticatedException.class, new NotAuthenticatedExceptionGlobalHandler(this));
	}

	@Override
	public void loadCurrentUserInformation(final UserInformationLoadCallback callback) {
		dispatchService.dispatch(new CurrentUserInformationRequest(), new DispatchCallback<CurrentUserInformationResponse>() {
			@Override
			public void onSuccess(final CurrentUserInformationResponse result) {
				final User user = result.getUser();

				updateCurrentUser(user);

				callback.onUserInformationLoaded(currentUserId);
				notifyUserInformationLoadToUserAuthenticationListeners();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onUnexpectedFailure(caught);
			}
		});
	}

	@Override
	public void authenticate(final String username, final String password, final UserAuthenticationCallback callback) {
		dispatchService.dispatch(new AuthenticationRequest(username, password), new DispatchCallback<AuthenticationResponse>() {

			@Override
			public void onSuccess(final AuthenticationResponse result) {
				updateCurrentUser(result.getUser());

				callback.onUserAuthenticatedSuccessfully(username, currentUserId);
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
	public void resetPasswordFor(final String username, final ResetPasswordCallback callback) {
		dispatchService.dispatch(new PasswordResetRequest(username), new DispatchCallback<VoidResult>() {

			@Override
			public void onSuccess(final VoidResult result) {
				callback.onUserPasswordResetSuccessfully();
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
				callback.onUserLogout();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				onUserLogout();
				callback.onUserLogout();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
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
				} else callback.onUnexpectedFailure(caught);
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

	private void notifyUserInformationLoadToUserAuthenticationListeners() {
		for (final UserAuthenticationListener listener : userAuthenticatedListeners) {
			listener.onUserInformationLoaded();
		}
	}

	private void notifyLogoutToUserAuthenticationListeners() {
		for (final UserAuthenticationListener listener : userAuthenticatedListeners) {
			listener.onUserLoggedOut();
		}
	}

	@Override
	public UUID getCurrentUserId() {
		return currentUserId;
	}

	private void processUserInformationUpdate(final UserInformationChangeEvent event) {
		if (currentUserId == null || !currentUserId.equals(event.getUserId())) logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {
				AuthenticationServiceImpl.this.onUserLogout();
			}

			@Override
			public void onFailure(final Throwable caught) {}
		});

		isSuperUser = event.isSuperUser();
	}

	private void updateCurrentUser(final User user) {
		ClientServices.get().metrics().onUserLogin(user);
		currentUserId = user.getId();
		isSuperUser = user.isSuperUser();
	}

	@Override
	public boolean isUserAvailable() {
		return getCurrentUserId() != null;
	}

	@Override
	public void onUserLogout() {
		resetCurrentUsetAndGoTo(new LoginPlace());
	}

	@Override
	public void onUserLoginRequired(final Place destinationPlace) {
		resetCurrentUsetAndGoTo(new LoginPlace(destinationPlace));
	}

	private void resetCurrentUsetAndGoTo(final Place destinationPlace) {
		currentUserId = null;
		isSuperUser = false;
		ClientServices.get().metrics().onUserLogout();

		notifyLogoutToUserAuthenticationListeners();
		applicationPlaceController.goTo(destinationPlace);
	}

	@Override
	public boolean isCurrentUserSuperUser() {
		return isSuperUser;
	}

}
