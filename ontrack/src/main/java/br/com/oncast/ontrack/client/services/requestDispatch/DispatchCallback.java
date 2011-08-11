package br.com.oncast.ontrack.client.services.requestDispatch;

public interface DispatchCallback<T> {

	void onFailure(Throwable caught);

	void onRequestCompletition(T response);
}
