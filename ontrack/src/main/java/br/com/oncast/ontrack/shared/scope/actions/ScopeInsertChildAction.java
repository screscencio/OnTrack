package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ScopeInsertChildAction implements ScopeInsertAction {

	private UUID selectedScopeId;
	private UUID newScopeId;

	public ScopeInsertChildAction(final Scope selectedScope) {
		this.selectedScopeId = selectedScope.getId();
	}

	protected ScopeInsertChildAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);
		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		selectedScope.add(newScope);
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
