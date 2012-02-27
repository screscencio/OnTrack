package br.com.oncast.ontrack.client.ui.components.scopetree;

import java.util.HashMap;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemBindReleaseEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareEffortEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareProgressEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemDeclareValueEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionStartEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	@IgnoredByDeepEquality
	private final HashMap<Scope, ScopeTreeItem> scopeItemCacheMap = new HashMap<Scope, ScopeTreeItem>();

	private final ScopeTreeItemWidget scopeItemWidget;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(scopeItemWidget = new ScopeTreeItemWidget(scope, new ScopeTreeItemWidgetEditionHandler() {

			@Override
			public void onEditionStart() {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionStartEvent(ScopeTreeItem.this));
			}

			@Override
			public void onEditionEnd(final String pattern) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionEndEvent(ScopeTreeItem.this, pattern));
			}

			@Override
			public void onEditionCancel() {
				getTree().setSelectedItem(ScopeTreeItem.this);
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionCancelEvent());
			}

			@Override
			public void onDeselectTreeItemRequest() {
				getTree().setSelectedItem(null);
			}

			@Override
			public void bindRelease(final String releaseDescription) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemBindReleaseEvent(getReferencedScope().getId(), releaseDescription));
			}

			@Override
			public void declareProgress(final String progressDescription) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemDeclareProgressEvent(getReferencedScope().getId(), progressDescription));
			}

			@Override
			public void declareEffort(final String effortDescription) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemDeclareEffortEvent(getReferencedScope().getId(), effortDescription));
			}

			@Override
			public void declareValue(final String valueDescription) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemDeclareValueEvent(getReferencedScope().getId(), valueDescription));
			}

			@Override
			public void onEditionMenuClose() {
				if (ScopeTreeItem.this.getTree().getSelectedItem() != null) return;
				ScopeTreeItem.this.select();
			}
		}));

		setReferencedScope(scope);
	}

	public void mountTwoLevels() {
		assureChildrenWasAdded();
		for (int i = 0; i < getChildCount(); i++)
			getChild(i).assureChildrenWasAdded();
	}

	@Override
	public void insertItem(final int beforeIndex, final TreeItem item) throws IndexOutOfBoundsException {
		super.insertItem(beforeIndex, item);
		final ScopeTreeItem scopeItem = (ScopeTreeItem) item;
		scopeItemCacheMap.put(scopeItem.getReferencedScope(), scopeItem);
	}

	@Override
	public void removeItem(final TreeItem item) {
		super.removeItem(item);
		scopeItemCacheMap.remove(((ScopeTreeItem) item).getReferencedScope());
	}

	private void assureChildrenWasAdded() {
		final List<Scope> children = this.getReferencedScope().getChildren();
		for (int i = 0; i < children.size(); i++) {
			final Scope childScope = children.get(i);
			final ScopeTreeItem childItem = scopeItemCacheMap.get(childScope);
			if (childItem == null) {
				final ScopeTreeItem item = new ScopeTreeItem(childScope);
				this.insertItem(i, item);
				scopeItemCacheMap.put(childScope, item);
				continue;
			}

			if (this.getChildIndex(childItem) != i) {
				this.removeItem(childItem);
				this.insertItem(i, childItem);
			}
		}

		for (int i = children.size(); i < this.getChildCount(); i++) {
			final ScopeTreeItem childItem = this.getChild(i);
			this.removeItem(childItem);
			scopeItemCacheMap.remove(childItem.getReferencedScope());
		}
	}

	protected void select() {
		getTree().setSelectedItem(this);
	}

	public boolean isRoot() {
		return getParentItem() == null;
	}

	public void enterEditMode() {
		scopeItemWidget.switchToEditionMode();
		final Tree tree = getTree();
		if (tree != null) tree.setSelectedItem(null);
	}

	@Override
	public ScopeTreeItem getChild(final int index) {
		return (ScopeTreeItem) super.getChild(index);
	}

	@Override
	public ScopeTreeItem getParentItem() {
		return (ScopeTreeItem) super.getParentItem();
	}

	// TODO Analise deprecating (thus removing) this method and using 'getScopeTreeItemWidget().setScope()' instead.
	public void setReferencedScope(final Scope scope) {
		scopeItemWidget.setScope(scope);
	}

	// TODO Analise deprecating (thus removing) this method and using 'getScopeTreeItemWidget().getScope()' instead.
	public Scope getReferencedScope() {
		return scopeItemWidget.getScope();
	}

	// TODO Analise using (maybe deprecating this method) 'Tree#ensureSelectedItemVisible()' method, that ensures that the currently-selected item is visible,
	// opening its parents and scrolling the tree as necessary.
	public void setHierarchicalState(final boolean state) {
		ScopeTreeItem item = this;

		while (item != null) {
			if (!item.getState()) item.setState(state);
			item = item.getParentItem();
		}
	}

	public ScopeTreeItemWidget getScopeTreeItemWidget() {
		return scopeItemWidget;
	}
}