package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeMoveRightAction implements ScopeMoveAction {

	private UUID selectedScopeId;
	private int position;
	private boolean wasIndexSet;

	public ScopeMoveRightAction(final UUID selectedScopeId) {
		this.selectedScopeId = selectedScopeId;
		this.wasIndexSet = false;
		this.position = -1;
	}

	public ScopeMoveRightAction(final UUID selectedScopeId, final int position) {
		this.selectedScopeId = selectedScopeId;
		this.position = position;
		this.wasIndexSet = true;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveRightAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		if (isFirstNode(parent.getChildIndex(selectedScope))) throw new UnableToCompleteActionException(
				"The action cannot be processed because there is no node where this node could be moded into.");

		final Scope upperSibling = parent.getChildren().get(parent.getChildIndex(selectedScope) - 1);
		selectedScope.getParent().remove(selectedScope);
		if (wasIndexSet) upperSibling.add(position, selectedScope);
		else upperSibling.add(selectedScope);

		return new ScopeMoveLeftAction(selectedScopeId);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}