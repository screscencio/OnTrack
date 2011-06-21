package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeInsertChildAction implements ScopeInsertAction {

	private UUID selectedScopeId;
	private UUID newScopeId;
	private String pattern;

	public ScopeInsertChildAction(final Scope selectedScope, final String pattern) {
		this.selectedScopeId = selectedScope.getId();
		this.pattern = pattern;
	}

	protected ScopeInsertChildAction() {}

	@Override
	public void execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(selectedScopeId);

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		selectedScope.add(newScope);

		new ScopeUpdateAction(newScope, pattern).execute(context);
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
