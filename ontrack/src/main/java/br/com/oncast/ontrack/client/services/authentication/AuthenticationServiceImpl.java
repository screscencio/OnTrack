package br.com.oncast.ontrack.client.services.authentication;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.login.ResetPasswordCallback;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordResetRequest;
import br.com.oncast.ontrack.shared.services.user.UserInformationUpdateEvent;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

public class AuthenticationServiceImpl implements AuthenticationService {

	protected static final ClientErrorMessages MESSAGES = GWT.create(ClientErrorMessages.class);

	private final Set<UserAuthenticationListener> userAuthenticatedListeners;

	private final ApplicationPlaceController applicationPlaceController;

	private final DispatchService dispatchService;

	private User currentUser;

	private final ClientAlertingService alertingService;

	public AuthenticationServiceImpl(final DispatchService dispatchService, final ApplicationPlaceController applicationPlaceController, final ServerPushClientService serverPushClientService,
			final ClientAlertingService alertingService) {
		this.dispatchService = dispatchService;
		this.applicationPlaceController = applicationPlaceController;
		this.alertingService = alertingService;
		userAuthenticatedListeners = new HashSet<UserAuthenticationListener>();

		serverPushClientService.registerServerEventHandler(UserInformationUpdateEvent.class, new ServerPushEventHandler<UserInformationUpdateEvent>() {
			@Override
			public void onEvent(final UserInformationUpdateEvent event) {
				processUserDataUpdate(event);
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

				callback.onUserInformationLoaded(currentUser.getId());
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

				callback.onUserAuthenticatedSuccessfully(username, currentUser.getId());
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
	public void logout() {
		dispatchService.dispatch(new DeAuthenticationRequest(), new DispatchCallback<VoidResult>() {

			@Override
			public void onSuccess(final VoidResult result) {
				onUserLogout();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				alertingService.showError(MESSAGES.logoutFailed());
				onUserLogout();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {}
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
		return currentUser.getId();
	}

	private void processUserDataUpdate(final UserInformationUpdateEvent event) {
		if (!event.getUser().equals(currentUser)) return;

		currentUser = event.getUser();
	}

	private void updateCurrentUser(final User user) {
		currentUser = user;
	}

	@Override
	public boolean isUserAvailable() {
		return currentUser != null;
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
		currentUser = null;

		notifyLogoutToUserAuthenticationListeners();
		applicationPlaceController.goTo(destinationPlace);
	}

	@Override
	public boolean canCurrentUserManageProjects() {
		return currentUser.getGlobalProfile().hasPermissionsOf(Profile.PROJECT_MANAGER);
	}

}
