package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.TreeStructure;

public class MoveRightScopeAction<T extends TreeStructure<T>> implements TreeStructureAction {

	private final T selectedTreeStructure;

	public MoveRightScopeAction(final T selectedTreeStructure) {
		this.selectedTreeStructure = selectedTreeStructure;
	}

	@Override
	public void execute() {
		if (selectedTreeStructure.isRoot()) return;
		if (selectedTreeStructure.getIndex() == 0) return;

		final TreeStructure<T> sibling = selectedTreeStructure.getParent().getChildren().get(selectedTreeStructure.getIndex() - 1);
		selectedTreeStructure.getParent().remove(selectedTreeStructure);
		sibling.add(selectedTreeStructure);
	}
}