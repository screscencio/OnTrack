package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;

public interface ScopeTreeItemWidgetEditionHandler {

	void onEditionStart();

	void onEditionEnd(String newValue);

	void onEditionCancel();

	/**
	 * IMPORTANT Workaround for ignoring the click event when the user is editing a tree item.
	 * In this case, if a user is editing and clicks inside the edition box,
	 * this event should not select any item in the tree, because if it select a item,
	 * the user gets unable to use the arrow keys for navigating inside the edition box.
	 */
	void onDeselectTreeItemRequest();

	void bindRelease(String releaseDescription);

	void onEditionMenuClose();

	void declareProgress(String progressDescription);

	void declareEffort(String effortDescription);

	void declareValue(String valueToDeclare);

	void addTag(String description, Color bgColor, Color fgColor);
}
