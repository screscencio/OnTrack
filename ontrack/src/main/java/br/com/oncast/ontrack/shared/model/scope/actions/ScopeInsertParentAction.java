package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.mapping.annotations.MapTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ScopeInsertParentActionEntity.class)
public class ScopeInsertParentAction implements ScopeInsertAction {

	private UUID referenceId;
	private UUID newScopeId;
	private String pattern;

	public ScopeInsertParentAction(final UUID selectedScopeId, final String pattern) {
		this.referenceId = selectedScopeId;
		this.pattern = pattern;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertParentAction() {}

	@Override
	public ScopeInsertParentRollbackAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a father for a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		parent.add(index, newScope);
		newScope.add(selectedScope);

		new ScopeUpdateAction(newScopeId, pattern).execute(context);
		return new ScopeInsertParentRollbackAction(newScopeId, referenceId, pattern);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}
}