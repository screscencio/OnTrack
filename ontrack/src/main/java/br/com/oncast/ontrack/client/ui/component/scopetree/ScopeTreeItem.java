package br.com.oncast.ontrack.client.ui.component.scopetree;

import br.com.oncast.ontrack.client.ui.component.scopetree.events.ScopeTreeItemEditionEvent;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeItemWidgetEditionHandler;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	private final ScopeTreeItemWidget descriptionLabel;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(descriptionLabel = new ScopeTreeItemWidget(scope.getDescription(), new ScopeTreeItemWidgetEditionHandler() {

			@Override
			public void onEdit(final String newContent) {
				ScopeTreeItem.this.getTree().fireEvent(new ScopeTreeItemEditionEvent(ScopeTreeItem.this, newContent));
			}
		}));
		setUserObject(scope);

		for (final Scope childScope : scope.getChildren()) {
			addItem(new ScopeTreeItem(childScope));
		}
	}

	public boolean isRoot() {
		return getParentItem() == null;
	}

	public Scope getReferencedScope() {
		return (Scope) super.getUserObject();
	}

	public String getDescription() {
		return descriptionLabel.getValue();
	}

	public void setDescription(final String description) {
		descriptionLabel.setValue(description);
	}

	public void enterEditMode() {
		descriptionLabel.switchToEditionMode();
	}

	@Override
	public ScopeTreeItem getChild(final int index) {
		return (ScopeTreeItem) super.getChild(index);
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ScopeTreeItem)) return false;
		final ScopeTreeItem otherTreeItem = (ScopeTreeItem) other;

		if (!this.getDescription().equals(otherTreeItem.getDescription())) return false;
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
}