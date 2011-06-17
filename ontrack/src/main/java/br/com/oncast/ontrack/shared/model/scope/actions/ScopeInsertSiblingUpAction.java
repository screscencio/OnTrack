package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeInsertSiblingUpAction implements ScopeInsertSiblingAction {
	private UUID selectedScopeId;
	private UUID newScopeId;

	public ScopeInsertSiblingUpAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
	}

	protected ScopeInsertSiblingUpAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope), newScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		new ScopeRemoveAction(context.findScope(newScopeId)).execute(context);
	}

	@Override
	public UUID getReferenceId() {
		return selectedScopeId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}
}
