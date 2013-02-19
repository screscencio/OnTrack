package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationCallback;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final UserAuthenticationCallback authenticationCallback;
	private final LoginView view;

	public LoginActivity(final Place destinationPlace) {
		this.view = new LoginPanel(this);

		this.authenticationCallback = new UserAuthenticationCallback() {

			@Override
			public void onUserAuthenticatedSuccessfully(final String username, final UUID userId) {
				view.enable();
				SERVICE_PROVIDER.getClientStorageService().storeLastUserEmail(username);
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(destinationPlace);
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				view.enable();
				SERVICE_PROVIDER.getClientAlertingService().showError(ClientServiceProvider.getInstance().getClientErrorMessages().unexpectedError());
			}

			@Override
			public void onIncorrectCredentialsFailure() {
				view.enable();
				view.onIncorrectCredentials();
				SERVICE_PROVIDER.getClientAlertingService().showError(ClientServiceProvider.getInstance().getClientErrorMessages().incorrectUserOrPassword());
			}

		};
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(view.asWidget());
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.asWidget());
		view.setUsername(SERVICE_PROVIDER.getClientStorageService().loadLastUserEmail(""));
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientAlertingService().clearAlertingParentWidget();
	}

	@Override
	public void onAuthenticationRequest(final String username, final String password) {
		view.disable();
		SERVICE_PROVIDER.getAuthenticationService().authenticate(username, password, authenticationCallback);
	}
}
