package br.com.oncast.ontrack.client.ui.places.planning.interation;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.ui.places.planning.authentication.ChangePasswordForm;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;

public class PlanningAuthenticationRequestHandler {

	private final AuthenticationService authenticationService;
	private final PlanningActivityListener planningActivityListener;

	public PlanningAuthenticationRequestHandler(final AuthenticationService authenticationService, final PlanningActivityListener planningActivityListener) {
		this.authenticationService = authenticationService;
		this.planningActivityListener = planningActivityListener;
	}

	public void logoutCurrentUser() {
		authenticationService.logoutCurrentUser(new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				planningActivityListener.onLoggedOut();
			}

			@Override
			public void onFailure(final Throwable caught) {
				// FIXME Threat this error.
			}
		});
	}

	public void changeUserPassword(final ChangePasswordForm sourceWidget, final String oldPassword, final String newPassword) {
		authenticationService.changeCurrentUserPassword(oldPassword, newPassword,
				new DispatchCallback<Void>() {

					@Override
					public void onRequestCompletition(final Void response) {
						sourceWidget.setInfoMessage("Password changed succefully.");
					}

					@Override
					public void onFailure(final Throwable caught) {
						if (caught instanceof IncorrectPasswordException) {
							sourceWidget.setErrorMessage("Incorrect old password.");
						}
					}
				});
	}

}
