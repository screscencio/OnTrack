package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem {

	public ScopeTreeItem(final Scope scope) {
		super(new Label(scope.getDescription()));
		setUserObject(scope);

		for (final Scope childScope : scope.getChildren()) {
			addItem(new ScopeTreeItem(childScope));
		}
	}

	public boolean isRoot() {
		return getParentItem() == null;
	}
}
