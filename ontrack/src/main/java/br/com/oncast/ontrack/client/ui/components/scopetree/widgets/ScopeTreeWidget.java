package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.Iterator;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeWidget extends Composite implements DeeplyComparable {

	private final Tree tree;

	public ScopeTreeWidget(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		initWidget(tree = new Tree());
		tree.addKeyUpHandler(interactionHandler);

		tree.addHandler(new ScopeTreeItemEditionEndEventHandler() {

			@Override
			public void onItemUpdateRequest(final ScopeTreeItem item, final String value) {
				interactionHandler.onItemUpdateRequest(item, value);
			}
		}, ScopeTreeItemEditionEndEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionCancelEventHandler() {

			@Override
			public void onItemEditCancelation() {
				interactionHandler.onItemEditCancelation();
			}
		}, ScopeTreeItemEditionCancelEvent.getType());
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
	public ScopeTreeItem getScopeTreeItemFor(final UUID scopeId) throws ScopeNotFoundException {
		final Iterator<TreeItem> treeItemIterator = tree.treeItemIterator();
		while (treeItemIterator.hasNext()) {
			final ScopeTreeItem item = (ScopeTreeItem) treeItemIterator.next();
			if (item.getReferencedScope().getId().equals(scopeId)) { return item; }
		}
		throw new ScopeNotFoundException("It was not possible to find any tree item for the given scope.");
	}

	@Override
	public boolean deepEquals(final Object other) {
		if (!(other instanceof ScopeTreeWidget)) return false;
		final ScopeTreeWidget otherTree = (ScopeTreeWidget) other;

		final int itemCount = tree.getItemCount();
		final int otherTreeItemCount = otherTree.getItemCount();
		if (itemCount != otherTreeItemCount) return false;

		for (int i = 0; i < itemCount; i++) {
			if (!otherTree.getItem(i).deepEquals(tree.getItem(i))) return false;
		}

		return true;
	}

	public void setSelectedItem(final ScopeTreeItem treeItem) {
		tree.setSelectedItem(treeItem);
	}
}
