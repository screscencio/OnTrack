package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationCallback;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();
	private final UserAuthenticationCallback authenticationCallback;
	private final LoginView view;

	public LoginActivity(final Place destinationPlace) {
		this.view = new LoginPanel(this);

		this.authenticationCallback = new UserAuthenticationCallback() {

			@Override
			public void onUserAuthenticatedSuccessfully(final String username, final UUID userId) {
				view.enable();
				SERVICE_PROVIDER.storage().storeLastUserEmail(username);
				SERVICE_PROVIDER.placeController().goTo(destinationPlace);
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				view.enable();
				SERVICE_PROVIDER.alerting().showError(ClientServices.get().errorMessages().unexpectedError());
			}

			@Override
			public void onIncorrectCredentialsFailure() {
				view.enable();
				view.onIncorrectCredentials();
				SERVICE_PROVIDER.alerting().showError(ClientServices.get().errorMessages().incorrectUserOrPassword());
			}

		};
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(view.asWidget());
		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.getAlertingContainer());
		view.setUsername(SERVICE_PROVIDER.storage().loadLastUserEmail(""));
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
	}

	@Override
	public void onAuthenticationRequest(final String username, final String password) {
		view.disable();
		SERVICE_PROVIDER.authentication().authenticate(username, password, authenticationCallback);
	}

	@Override
	public void onResetPasswordRequest(final String username) {
		view.disable();
		final ClientErrorMessages messages = ClientServices.get().errorMessages();
		SERVICE_PROVIDER.alerting().showInfo(messages.requestingNewPassword(username));
		SERVICE_PROVIDER.authentication().resetPasswordFor(username, new ResetPasswordCallback() {
			@Override
			public void onUserPasswordResetSuccessfully() {
				view.enable();
				SERVICE_PROVIDER.storage().storeLastUserEmail(username);
				SERVICE_PROVIDER.alerting().showSuccess(messages.passwordRequestSucessful());
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				view.enable();
				SERVICE_PROVIDER.alerting().showError(messages.unexpectedError());
			}

			@Override
			public void onIncorrectCredentialsFailure() {
				view.enable();
				view.onIncorrectUsername();
				SERVICE_PROVIDER.alerting().showError(messages.passwordRequestFailedDueToBadUsername());
			}
		});
	}
}
