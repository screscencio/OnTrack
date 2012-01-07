package br.com.oncast.ontrack.client.services.context;

public interface ProjectAuthorizationCallback {

	void onSuccess();

	void onFailure(Throwable caught);

}
