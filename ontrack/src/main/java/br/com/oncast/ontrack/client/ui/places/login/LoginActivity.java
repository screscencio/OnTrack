package br.com.oncast.ontrack.client.ui.places.login;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.login.interaction.LoginActivityListener;
import br.com.oncast.ontrack.client.ui.places.login.interaction.LoginRequestHandler;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class LoginActivity extends AbstractActivity {

	private final LoginRequestHandler loginRequestHandler;

	public LoginActivity(final AuthenticationService authenticationService,
			final ApplicationPlaceController placeController,
			final Place destinationPlace) {

		loginRequestHandler = new LoginRequestHandler(authenticationService, new LoginActivityListener() {

			@Override
			public void onLoggedIn() {
				placeController.goTo(destinationPlace);
			}
		});
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		panel.setWidget(createLoginWidget());
	}

	private Widget createLoginWidget() {
		return new LoginPanel(loginRequestHandler).asWidget();
	}

	@Override
	public void onStop() {
		// TODO Remove user from session?
	}

}
