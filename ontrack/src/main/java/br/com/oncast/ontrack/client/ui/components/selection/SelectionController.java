package br.com.oncast.ontrack.client.ui.components.selection;

public interface SelectionController {

	void selectNext();

	void setSelected(IsSelectable selectable);

	void deselectAll();

	void selectPrevious();

}
