package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

public interface MenuBoxItemFactory<T> {

	MenuBoxItem createItem(T bean);

	MenuBoxItem createCustomItem(String inputText);

}
