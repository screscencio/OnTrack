package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import br.com.oncast.ontrack.shared.beans.TreeStructure;

public class MoveDownScopeAction<T extends TreeStructure<T>> implements TreeStructureAction {

	private final T selectedTreeStructure;

	public MoveDownScopeAction(final T selectedTreeStructure) {
		this.selectedTreeStructure = selectedTreeStructure;
	}

	@Override
	public void execute() {
		if (selectedTreeStructure.isRoot()) return;

		final int index = selectedTreeStructure.getIndex();
		final TreeStructure<T> parent = selectedTreeStructure.getParent();
		if (!(parent.getChildren().size() - 1 > index)) return;

		parent.remove(selectedTreeStructure);
		parent.add(index + 1, selectedTreeStructure);
	}
}