package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.TreeStructure;

public class MoveUpScopeAction<T extends TreeStructure<T>> implements TreeStructureAction {

	private final T selectedTreeStructure;

	public MoveUpScopeAction(final T selectedTreeStructure) {
		this.selectedTreeStructure = selectedTreeStructure;
	}

	@Override
	public void execute() {
		if (selectedTreeStructure.isRoot()) return;

		final int index = selectedTreeStructure.getIndex();
		if (index == 0) return;

		final TreeStructure<T> parent = selectedTreeStructure.getParent();
		parent.remove(selectedTreeStructure);
		parent.add(index - 1, selectedTreeStructure);
	}
}
