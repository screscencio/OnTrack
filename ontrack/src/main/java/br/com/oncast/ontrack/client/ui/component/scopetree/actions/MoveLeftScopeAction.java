package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.TreeStructure;

public class MoveLeftScopeAction<T extends TreeStructure<T>> implements TreeStructureAction {

	private final T selectedTreeStructure;

	public MoveLeftScopeAction(final T selectedTreeStructure) {
		this.selectedTreeStructure = selectedTreeStructure;
	}

	@Override
	public void execute() {
		if (selectedTreeStructure.isRoot()) return;
		if (selectedTreeStructure.getParent().isRoot()) return;

		final TreeStructure<T> parent = selectedTreeStructure.getParent();
		parent.remove(selectedTreeStructure);
		parent.getParent().add(parent.getIndex() + 1, selectedTreeStructure);
	}
}
