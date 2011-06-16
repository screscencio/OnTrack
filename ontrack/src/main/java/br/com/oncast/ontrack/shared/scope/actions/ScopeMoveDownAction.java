package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeMoveDownAction implements ScopeMoveAction {

	private UUID selectedScopeId;

	public ScopeMoveDownAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
	}

	protected ScopeMoveDownAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isLastNode(index, parent)) throw new UnableToCompleteActionException("It is not possible to move down the node when it is the last node.");

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		new ScopeMoveUpAction(context.findScope(selectedScopeId)).execute(context);
	}

	private boolean isLastNode(final int index, final Scope parent) {
		return parent.getChildren().size() - 1 == index;
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}