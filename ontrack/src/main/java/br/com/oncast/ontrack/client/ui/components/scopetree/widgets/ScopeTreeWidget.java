package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.Iterator;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeWidget extends Composite {

	private final Tree tree;

	public ScopeTreeWidget(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		initWidget(tree = new Tree());
		tree.addKeyUpHandler(interactionHandler);
		tree.addHandler(interactionHandler, ScopeTreeItemEditionEvent.getType());
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

	private ScopeTreeItem getItem(final int index) {
		return (ScopeTreeItem) tree.getItem(index);
	}

	private int getItemCount() {
		return tree.getItemCount();
	}

	// TODO Examine refactoring this so that it uses a Map.
	public ScopeTreeItem getScopeTreeItemFor(final Scope scope) throws ScopeNotFoundException {
		final Iterator<TreeItem> treeItemIterator = tree.treeItemIterator();
		while (treeItemIterator.hasNext()) {
			final ScopeTreeItem item = (ScopeTreeItem) treeItemIterator.next();
			if (item.getReferencedScope().equals(scope)) { return item; }
		}
		throw new ScopeNotFoundException("It was not possible to find any tree item for the given scope.");
	}

	// TODO Create another 'equals' like method for testing purposes. Refactor test to not use this 'equals' method.
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ScopeTreeWidget)) return false;
		final ScopeTreeWidget otherTree = (ScopeTreeWidget) other;

		final int itemCount = tree.getItemCount();
		final int otherTreeItemCount = otherTree.getItemCount();
		if (itemCount != otherTreeItemCount) return false;

		for (int i = 0; i < itemCount; i++) {
			if (!otherTree.getItem(i).equals(tree.getItem(i))) return false;
		}

		return true;
	}

	public void setSelectedItem(final ScopeTreeItem treeItem) {
		tree.setSelectedItem(treeItem);
	}
}
