package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemBindReleaseEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemBindReleaseEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareEffortEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareEffortEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareProgressEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareProgressEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionStartEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionStartEventHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeItemAdoptionListener;

public class ScopeTreeWidget extends Composite {

	private final Tree tree;

	private final Map<UUID, ScopeTreeItem> itemMapCache = new HashMap<UUID, ScopeTreeItem>();

	public ScopeTreeWidget(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		initWidget(tree = new Tree());
		tree.addKeyUpHandler(interactionHandler);

		tree.addHandler(new ScopeTreeItemBindReleaseEventHandler() {

			@Override
			public void onBindReleaseRequest(final UUID scopeId, final String releaseDescription) {
				interactionHandler.onBindReleaseRequest(scopeId, releaseDescription);
			}
		}, ScopeTreeItemBindReleaseEvent.getType());

		tree.addHandler(new ScopeTreeItemDeclareEffortEventHandler() {

			@Override
			public void onDeclareEffortRequest(final UUID scopeId, final String effortDescription) {
				interactionHandler.onDeclareEffortRequest(scopeId, effortDescription);
			}

		}, ScopeTreeItemDeclareEffortEvent.getType());

		tree.addHandler(new ScopeTreeItemDeclareProgressEventHandler() {

			@Override
			public void onDeclareProgressRequest(final UUID scopeId, final String progressDescription) {
				interactionHandler.onDeclareProgressRequest(scopeId, progressDescription);
			}
		}, ScopeTreeItemDeclareProgressEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionStartEventHandler() {

			@Override
			public void onItemEditionStart(final ScopeTreeItem item) {
				interactionHandler.onItemEditionStart(item);
			}
		}, ScopeTreeItemEditionStartEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionEndEventHandler() {

			@Override
			public void onItemEditionEnd(final ScopeTreeItem item, final String value) {
				interactionHandler.onItemEditionEnd(item, value);
			}
		}, ScopeTreeItemEditionEndEvent.getType());

		tree.addHandler(new ScopeTreeItemEditionCancelEventHandler() {

			@Override
			public void onItemEditionCancel() {
				interactionHandler.onItemEditionCancel();
			}
		}, ScopeTreeItemEditionCancelEvent.getType());

		tree.setTreeItemAdoptionListener(new TreeItemAdoptionListener() {

			@Override
			public void onTreeItemAdopted(final TreeItem treeItem) {
				final ScopeTreeItem scopeTreeItem = ((ScopeTreeItem) treeItem);
				addRecursivelyToCache(scopeTreeItem);
			}

			@Override
			public void onTreeItemAbandoned(final TreeItem treeItem) {
				final ScopeTreeItem scopeTreeItem = ((ScopeTreeItem) treeItem);
				removeRecusivelyFromCache(scopeTreeItem);
			}

			private void addRecursivelyToCache(final ScopeTreeItem scopeTreeItem) {
				itemMapCache.put(scopeTreeItem.getScopeTreeItemWidget().getScope().getId(), scopeTreeItem);

				final int count = scopeTreeItem.getChildCount();
				for (int i = 0; i < count; i++) {
					addRecursivelyToCache(scopeTreeItem.getChild(i));
				}
			}

			private void removeRecusivelyFromCache(final ScopeTreeItem scopeTreeItem) {
				itemMapCache.remove(scopeTreeItem.getScopeTreeItemWidget().getScope().getId());

				final int count = scopeTreeItem.getChildCount();
				for (int i = 0; i < count; i++) {
					removeRecusivelyFromCache(scopeTreeItem.getChild(i));
				}
			}
		});
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
		if (getItemCount() > 0) setSelectedItem(getItem(0));
	}

	public ScopeTreeItem getItem(final int index) {
		return (ScopeTreeItem) tree.getItem(index);
	}

	public int getItemCount() {
		return tree.getItemCount();
	}

	public ScopeTreeItem findScopeTreeItem(final UUID scopeId) throws ScopeNotFoundException {
		final ScopeTreeItem scopeTreeItem = itemMapCache.get(scopeId);
		if (scopeTreeItem == null) throw new ScopeNotFoundException("It was not possible to find any tree item for the given scope.");
		return scopeTreeItem;
	}

	public void setSelectedItem(final ScopeTreeItem treeItem) {
		tree.setSelectedItem(treeItem);
	}
}
