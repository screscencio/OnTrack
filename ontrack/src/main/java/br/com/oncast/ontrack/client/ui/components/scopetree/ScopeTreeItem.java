package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionCancelEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEndEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem, DeeplyComparable {

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
	public boolean deepEquals(final Object other) {
		if (!(other instanceof ScopeTreeItem)) return false;
		return getReferencedScope().deepEquals(((ScopeTreeItem) other).getReferencedScope());
	}

	@Override
	public ScopeTreeItem getParentItem() {
		return (ScopeTreeItem) super.getParentItem();
	}

	public void setReferencedScope(final Scope scope) {
		scopeItemWidget.setScope(scope);
	}

	public Scope getReferencedScope() {
		return scopeItemWidget.getScope();
	}

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