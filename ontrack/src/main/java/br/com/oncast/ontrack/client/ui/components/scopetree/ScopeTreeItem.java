package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	private final ScopeTreeItemWidget scopeItemWidget;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(scopeItemWidget = new ScopeTreeItemWidget(scope, new ScopeTreeItemWidgetEditionHandler() {

			@Override
			public void onEdit(final String pattern) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionEvent(ScopeTreeItem.this, pattern));
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
	}

	@Override
	public ScopeTreeItem getChild(final int index) {
		return (ScopeTreeItem) super.getChild(index);
	}

	// TODO Refactor this so equals bases itself on the referenced scope
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ScopeTreeItem)) return false;
		final ScopeTreeItem otherTreeItem = (ScopeTreeItem) other;

		if (!this.getReferencedScope().equals(otherTreeItem.getReferencedScope())) return false;
		if (this.getChildCount() != otherTreeItem.getChildCount()) return false;

		for (int i = 0; i < this.getChildCount(); i++) {
			if (!this.getChild(i).equals(otherTreeItem.getChild(i))) return false;
		}
		return true;
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
}