package br.com.oncast.ontrack.client.services.authentication;

public interface UserLogoutCallback {

	void onUserLogout();

	void onFailure(Throwable caught);

}
