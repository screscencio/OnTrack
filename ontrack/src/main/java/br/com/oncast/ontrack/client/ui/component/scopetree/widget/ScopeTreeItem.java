package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import br.com.oncast.ontrack.client.ui.component.editableLabel.widget.EditableLabel;
import br.com.oncast.ontrack.client.ui.component.editableLabel.widget.EditionHandler;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.event.ScopeTreeItemEditionEvent;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	private final EditableLabel descriptionLabel;

	public ScopeTreeItem(final Scope scope) {
		super();
		this.setWidget(descriptionLabel = new EditableLabel(scope.getDescription(), new EditionHandler() {

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

		for (int i = 0; i < this.getChildCount(); i++) {
			if (!this.getChild(i).equals(otherTreeItem.getChild(i))) return false;
		}
		return true;
	}
}