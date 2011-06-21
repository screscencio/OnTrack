package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeInsertFatherAction implements ScopeInsertAction {
	private UUID selectedScopeId;
	private UUID newScopeId;
	private String pattern;

	public ScopeInsertFatherAction(final Scope selectedScope, final String pattern) {
		this.selectedScopeId = selectedScope.getId();
		this.pattern = pattern;
	}

	protected ScopeInsertFatherAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a father for a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		parent.add(index, newScope);
		newScope.add(selectedScope);

		new ScopeUpdateAction(newScope, pattern).execute(context);
	}

	@Override
	public void rollback(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope newScope = context.findScope(newScopeId);
		if (newScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		if (newScope.getChildren().size() <= 0) throw new UnableToCompleteActionException("It is not possible to rollback this action due to inconsistences.");

		removeFromRelease(newScope);

		final Scope child = newScope.getChildren().get(0);
		final Scope parent = newScope.getParent();
		final int index = parent.getChildIndex(newScope);
		parent.remove(newScope);

		newScope.clearChildren();
		parent.add(index, child);
	}

	private void removeFromRelease(final Scope newScope) {
		if (newScope.getRelease() != null) {
			newScope.getRelease().removeScope(newScope);
			newScope.setRelease(null);
		}
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