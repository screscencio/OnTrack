package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeMoveUpAction implements ScopeMoveAction {

	private UUID selectedScopeId;

	public ScopeMoveUpAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
	}

	protected ScopeMoveUpAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException("It is not possible to move up the first node.");

		parent.remove(selectedScope);
		parent.add(index - 1, selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		new ScopeMoveDownAction(context.findScope(selectedScopeId)).execute(context);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public UUID getScopeId() {
		return selectedScopeId;
	}
}