package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeInsertAsFatherAction implements ScopeInsertAction {
	private UUID scopeId;
	private UUID newScopeId;

	public ScopeInsertAsFatherAction(final Scope selectedScope) {
		this.scopeId = selectedScope.getId();
	}

	protected ScopeInsertAsFatherAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(scopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a father for a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		parent.add(index, newScope);
		newScope.add(selectedScope);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope newScope = context.findScope(newScopeId);
		if (newScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		if (newScope.getChildren().size() <= 0) throw new UnableToCompleteActionException("It is not possible to rollback this action due to inconsistences.");

		final Scope child = newScope.getChildren().get(0);
		final Scope parent = newScope.getParent();
		final int index = parent.getChildIndex(newScope);
		parent.remove(newScope);
		newScope.clearChildren();
		parent.add(index, child);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}
}