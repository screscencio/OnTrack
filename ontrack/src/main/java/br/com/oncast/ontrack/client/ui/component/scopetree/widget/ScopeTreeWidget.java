package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomGwtTree;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeWidget extends Composite {

	private final CustomGwtTree tree;

	public ScopeTreeWidget(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		initWidget(tree = new CustomGwtTree());
		tree.addKeyUpHandler(interactionHandler);
	}

	public void add(final ScopeTreeItem item) {
		tree.addItem(item);
	}

	public void add(final int beforeIndex, final ScopeTreeItem item) {
		tree.insertItem(beforeIndex, item.asTreeItem());
	}

	public void remove(final ScopeTreeItem item) {
		tree.removeItem(item);
	}

	public void clear() {
		tree.clear();
	}

	public TreeItem getSelected() {
		return tree.getSelectedItem();
	}

	public void setSelected(final ScopeTreeItem selected) {
		tree.setSelectedItem(selected);
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}
}
