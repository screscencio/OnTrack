package br.com.oncast.ontrack.client.services.authentication;

import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final RequestDispatchService requestService;
	private final UserProviderService userProviderService;

	public AuthenticationServiceImpl(final RequestDispatchService requestService, final UserProviderService userProviderService) {
		this.requestService = requestService;
		this.userProviderService = userProviderService;
	}

	@Override
	public boolean isUserLoggedIn() {
		return userProviderService.isCurrentUserAuthenticated();
	}

	@Override
	public void authenticateUser(final String login, final String password, final DispatchCallback<User> callback) {
		requestService.dispatch(new AuthenticationRequest(login, password), new DispatchCallback<User>() {

			@Override
			public void onRequestCompletition(final User response) {
				userProviderService.setAuthenticatedUser(response);
				callback.onRequestCompletition(response);
			}

			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	@Override
	public void logoutCurrentUser(final DispatchCallback<Void> dispatchCallback) {
		requestService.dispatch(new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				userProviderService.logoutCurrentUser();
				dispatchCallback.onRequestCompletition(response);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
			}
		});
	}

	@Override
	public void changeCurrentUserPassword(final String oldPassword, final String newPassword, final DispatchCallback<Void> dispatchCallback) {
		requestService.dispatch(new PasswordChangeRequest(userProviderService.getCurrentUserId(), oldPassword, newPassword), new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				dispatchCallback.onRequestCompletition(response);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
			}
		});

	}
}
