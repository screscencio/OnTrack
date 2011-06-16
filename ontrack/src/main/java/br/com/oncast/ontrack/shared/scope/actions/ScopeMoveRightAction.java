package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeMoveRightAction implements ScopeMoveAction {

	private final UUID selectedScopeId;

	public ScopeMoveRightAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
	}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException(
				"The action cannot be processed because there is no node where this node could be moded into.");

		final Scope upperSibling = parent.getChildren().get(index - 1);
		selectedScope.getParent().remove(selectedScope);
		upperSibling.add(selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		new ScopeMoveLeftAction(context.findScope(selectedScopeId)).execute(context);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}