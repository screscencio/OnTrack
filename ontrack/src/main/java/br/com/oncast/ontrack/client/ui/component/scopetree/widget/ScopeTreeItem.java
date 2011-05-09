package br.com.oncast.ontrack.client.ui.component.scopetree.widget;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.beans.Scope;
import br.com.oncast.ontrack.shared.beans.TreeStructure;

import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeItem extends TreeItem implements IsTreeItem, TreeStructure<ScopeTreeItem> {

	public ScopeTreeItem(final Scope scope) {
		super(new Label(scope.getDescription()));
		setUserObject(scope);

		for (final Scope childScope : scope.getChildren()) {
			addItem(new ScopeTreeItem(childScope));
		}
	}

	@Override
	public void add(final ScopeTreeItem treeStructure) {
		addItem(treeStructure);
	}

	@Override
	public void add(final int beforeIndex, final ScopeTreeItem treeStructure) {
		insertItem(beforeIndex, treeStructure);
	}

	@Override
	public void remove(final ScopeTreeItem treeStructure) {
		removeItem(treeStructure);
	}

	@Override
	public TreeStructure<ScopeTreeItem> getParent() {
		return (ScopeTreeItem) getParentItem();
	}

	@Override
	public int getIndex() {
		if (isRoot()) return 0;
		return getParent().getChildren().indexOf(this);
	}

	@Override
	public boolean isRoot() {
		return getParentItem() == null;
	}

	@Override
	public List<ScopeTreeItem> getChildren() {
		final List<ScopeTreeItem> resultList = new ArrayList<ScopeTreeItem>();
		final int childCount = super.getChildCount();
		for (int i = 0; i < childCount; i++)
			resultList.add((ScopeTreeItem) super.getChild(i));
		return resultList;
	}
}
