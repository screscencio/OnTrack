package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeItemEditionEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem, DeeplyComparable {

	private final ScopeTreeItemWidget scopeItemWidget;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(scopeItemWidget = new ScopeTreeItemWidget(scope, new ScopeTreeItemWidgetEditionHandler() {

			@Override
			public void onEdit(final String pattern) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionEvent(ScopeTreeItem.this, pattern));
			}

			@Override
			public void onCancel() {
				getTree().setSelectedItem(ScopeTreeItem.this);
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
}