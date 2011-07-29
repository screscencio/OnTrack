package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	private final ScopeTreeItemWidget scopeItemWidget;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(scopeItemWidget = new ScopeTreeItemWidget(scope, new ScopeTreeItemWidgetEditionHandler() {

			@Override
			public void onEditionEnd(final String pattern) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionEndEvent(ScopeTreeItem.this, pattern));
			}

			@Override
			public void onEditionCancel() {
				getTree().setSelectedItem(ScopeTreeItem.this);
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionCancelEvent());
			}
		}));

		setReferencedScope(scope);
		for (final Scope childScope : scope.getChildren()) {
			addItem(new ScopeTreeItem(childScope));
		}
	}

	public boolean isRoot() {
		return getParentItem() == null;
	}

	public void enterEditMode() {
		scopeItemWidget.switchToEditionMode();
		getTree().setSelectedItem(null);
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