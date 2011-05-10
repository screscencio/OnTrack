package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.shared.beans.Scope;

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

	public ScopeTreeItem getSelected() {
		return (ScopeTreeItem) tree.getSelectedItem();
	}

	public void setSelected(final ScopeTreeItem selected) {
		tree.setSelectedItem(selected);
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}

	public ScopeTreeItem getScopeTreeItemFor(final Scope scope) throws NotFoundException {
		final int itemCount = tree.getItemCount();
		for (int i = 0; i < itemCount; i++) {
			final TreeItem item = tree.getItem(i);
			final TreeItem result = recursiveSearch(scope, item);
			if (result != null) return (ScopeTreeItem) result;
		}
		throw new NotFoundException();
	}

	private TreeItem recursiveSearch(final Scope scope, final TreeItem item) {
		if (item.getUserObject().equals(scope)) return item;
		final int childCount = item.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final TreeItem result = recursiveSearch(scope, item.getChild(i));
			if (result != null) return result;
		}
		return null;
	}
}
