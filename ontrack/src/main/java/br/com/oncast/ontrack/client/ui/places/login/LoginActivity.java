package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationCallback;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	private final ClientErrorMessages messages = GWT.create(ClientErrorMessages.class);
	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final UserAuthenticationCallback authenticationCallback;
	private final LoginView view;

	public LoginActivity(final Place destinationPlace) {
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
		this.view = new LoginPanel(this);

		this.authenticationCallback = new UserAuthenticationCallback() {

			@Override
			public void onUserAuthenticatedSuccessfully(final User user) {
				view.enable();
				SERVICE_PROVIDER.getClientStorageService().storeLastUserEmail(user.getEmail());
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(destinationPlace);
			}

			@Override
			public void onUnexpectedFailure(final Throwable caught) {
				// TODO Improve feedback message.
				view.enable();
				SERVICE_PROVIDER.getClientAlertingService().showError(messages.unexpectedError());
			}

			@Override
			public void onIncorrectCredentialsFailure() {
				// TODO Improve feedback message.
				view.enable();
				view.onIncorrectCredentials();
				SERVICE_PROVIDER.getClientAlertingService().showError(messages.incorrectUserOrPassword());
			}

		};
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(view.asWidget());
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.asWidget());
		view.setUsername(SERVICE_PROVIDER.getClientStorageService().loadLastUserEmail(""));
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadEnd();
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
