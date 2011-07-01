package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentRollbackActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertParentRollbackActionEntity.class)
public class ScopeInsertParentRollbackAction implements ScopeAction {

	private UUID referenceId;
	private UUID newScopeId;
	private String pattern;

	public ScopeInsertParentRollbackAction(final UUID newScopeId, final UUID selectedScopeId, final String pattern) {
		this.newScopeId = newScopeId;
		this.referenceId = selectedScopeId;
		this.pattern = pattern;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertParentRollbackAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
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

		return new ScopeInsertParentAction(referenceId, pattern);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	private void removeFromRelease(final Scope newScope) {
		if (newScope.getRelease() != null) {
			newScope.getRelease().removeScope(newScope);
			newScope.setRelease(null);
		}
	}
}
