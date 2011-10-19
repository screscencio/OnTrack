package br.com.oncast.ontrack.client.ui.places.login.interaction;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.ui.places.login.LoginPanel;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

public class LoginRequestHandler {

	private final AuthenticationService authenticationService;
	private final LoginActivityListener loginListener;

	public LoginRequestHandler(final AuthenticationService authenticationService, final LoginActivityListener loginListener) {
		this.authenticationService = authenticationService;
		this.loginListener = loginListener;
	}

	public void authenticateUser(final LoginPanel sourceWidget, final String email, final String password) {
		authenticationService.authenticateUser(email, password,
				new DispatchCallback<User>() {

					@Override
					public void onRequestCompletition(final User response) {
						loginListener.onLoggedIn();
					}

					@Override
					public void onFailure(final Throwable caught) {
						if (caught instanceof UserNotFoundException) {
							sourceWidget.setErrorMessage("No user was not found with this e-mail.");
						}
						if (caught instanceof IncorrectPasswordException) {
							sourceWidget.setErrorMessage("Incorrect password for this user.");
						}
					}
				});
	}
}
