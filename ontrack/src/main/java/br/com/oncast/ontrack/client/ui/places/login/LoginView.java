package br.com.oncast.ontrack.client.ui.places.login;

import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView {

	void setErrorMessage(final String message);

	IsWidget asWidget();

	public interface Presenter {
		void onAuthenticationRequest(String username, String password);
	}

	void disable();

	void enable();
}
