package br.com.oncast.ontrack.client.ui.generalwidgets;

public interface IncrementalAdditionListener<T> {

	void onItemAdded(T item);

	void onFinished(boolean allItemsAdded);

}
