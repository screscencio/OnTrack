package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeMoveLeftAction implements ScopeMoveAction {

	private UUID selectedScopeId;

	public ScopeMoveLeftAction(final UUID selectedScopeId) {
		this.selectedScopeId = selectedScopeId;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveLeftAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (selectedScope.getParent().isRoot()) throw new UnableToCompleteActionException("It is not possible to move left when a node parent is a root node.");

		final Scope parent = selectedScope.getParent();
		final Scope grandParent = parent.getParent();
		final int index = parent.getChildIndex(selectedScope);

		parent.remove(selectedScope);
		grandParent.add(grandParent.getChildIndex(parent) + 1, selectedScope);

		return new ScopeMoveRightAction(selectedScopeId, index);
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}
}
