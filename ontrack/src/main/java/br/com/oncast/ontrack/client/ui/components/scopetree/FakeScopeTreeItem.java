package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.TreeItem;

public class FakeScopeTreeItem extends ScopeTreeItem implements IsTreeItem {

	private FakeScopeTreeItem() {}

	public static ScopeTreeItem get() {
		return new FakeScopeTreeItem();
	}

	@Override
	public boolean isFake() {
		return true;
	};

	@Override
	public boolean mountTwoLevels() {
		return false;
	}

	@Override
	public void insertItem(final int beforeIndex, final TreeItem item) throws IndexOutOfBoundsException {}

	@Override
	public void removeItem(final TreeItem item) {}

	@Override
	protected void select() {}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public void enterEditMode() {}

	@Override
	public ScopeTreeItem getChild(final int index) {
		return this;
	}

	@Override
	public ScopeTreeItem getParentItem() {
		return this;
	}

	@Override
	public void setReferencedScope(final Scope scope) {}

	@Override
	public Scope getReferencedScope() {
		return null;
	}

	@Override
	public ScopeTreeItem setHierarchicalState(final boolean state) {
		return this;
	}

	@Override
	public ScopeTreeItemWidget getScopeTreeItemWidget() {
		return null;
	}

	@Override
	public void showDetailsIcon(final boolean b) {}

	@Override
	public void showOpenImpedimentIcon(final boolean hasOpenImpediments) {}

	@Override
	public void addSelectedMember(final UserRepresentation member, final Color selectionColor) {}

	@Override
	public void removeSelectedMember(final UserRepresentation member) {}

}