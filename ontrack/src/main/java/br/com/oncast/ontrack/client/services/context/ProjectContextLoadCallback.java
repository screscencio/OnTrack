package br.com.oncast.ontrack.client.services.context;

public interface ProjectContextLoadCallback {

	void onProjectContextLoaded();

	void onProjectNotFound();

	void onUnexpectedFailure(Throwable cause);
}
