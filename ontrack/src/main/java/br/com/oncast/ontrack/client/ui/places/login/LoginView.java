package br.com.oncast.ontrack.client.ui.places.login;

import com.google.gwt.user.client.ui.Widget;

public interface LoginView {

	Widget asWidget();

	public interface Presenter {
		void onAuthenticationRequest(String username, String password);
	}

	void disable();

	void enable();

	void onIncorrectCredentials();

	void setUsername(String username);
}
