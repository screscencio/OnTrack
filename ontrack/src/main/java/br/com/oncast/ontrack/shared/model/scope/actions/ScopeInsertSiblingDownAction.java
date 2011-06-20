package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeInsertSiblingDownAction implements ScopeInsertSiblingAction {
	private final UUID selectedScopeId;
	private UUID newScopeId;
	private final String pattern;

	public ScopeInsertSiblingDownAction(final Scope selectedScope, final String pattern) {
		this.pattern = pattern;
		this.selectedScopeId = selectedScope.getId();
	}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope) + 1, newScope);

		new ScopeUpdateAction(newScope, pattern).execute(context);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope newScope = context.findScope(newScopeId);
		new ScopeRemoveAction(newScope).execute(context);
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
