package br.com.oncast.ontrack.client.services.communication;

public interface DispatchCallback<T> {

	void onFailure(Throwable cause);

	void onRequestCompletition(T result);
}
