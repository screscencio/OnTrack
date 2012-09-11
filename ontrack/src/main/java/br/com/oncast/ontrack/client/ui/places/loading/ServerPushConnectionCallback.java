package br.com.oncast.ontrack.client.ui.places.loading;

public interface ServerPushConnectionCallback {

	void connected();

	void uncaughtExeption(Throwable cause);

}
