package br.com.oncast.ontrack.client.ui.components.appmenu.interaction;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.PlanningActivityListener;
import br.com.oncast.ontrack.client.services.authentication.UserLogoutCallback;
import br.com.oncast.ontrack.client.services.authentication.UserPasswordChangeCallback;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ChangePasswordForm;

import com.google.gwt.user.client.Window;

// TODO Review this class, its relationships (specially with PlanningActivity and ApplicationMenu) and responsibilities.
public class PlanningAuthenticationRequestHandler {

	private final AuthenticationService authenticationService;
	private final PlanningActivityListener planningActivityListener;

	public PlanningAuthenticationRequestHandler(final AuthenticationService authenticationService, final PlanningActivityListener planningActivityListener) {
		this.authenticationService = authenticationService;
		this.planningActivityListener = planningActivityListener;
	}

	public void logoutUser() {
		authenticationService.logout(new UserLogoutCallback() {

			@Override
			public void onUserLogout() {
				planningActivityListener.onLoggedOut();
			}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Threat this error properly. Maybe even call the ErrorService.
				Window.alert("It was not possible to log the user out properly.");
			}
		});
	}

	public void changeUserPassword(final ChangePasswordForm sourceWidget, final String currentPassword, final String newPassword) {
		authenticationService.changePassword(currentPassword, newPassword, new UserPasswordChangeCallback() {

			@Override
			public void onUserPasswordChangedSuccessfully() {
				// TODO Improve feedback message.
				sourceWidget.setInfoMessage("Password changed succefully.");
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				// TODO Improve feedback message.
				sourceWidget.setErrorMessage("Unexpected error.");

			}

			@Override
			public void onIncorrectUserPasswordFailure() {
				// TODO Improve feedback message.
				sourceWidget.setErrorMessage("Incorrect old password.");
			}
		});
	}

}
