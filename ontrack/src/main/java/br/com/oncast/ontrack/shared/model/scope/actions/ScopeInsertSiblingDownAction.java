package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingDownActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertSiblingDownActionEntity.class)
public class ScopeInsertSiblingDownAction implements ScopeInsertSiblingAction {

	private UUID referenceId;
	private UUID newScopeId;
	private String pattern;

	public ScopeInsertSiblingDownAction(final Scope selectedScope, final String pattern) {
		this.referenceId = selectedScope.getId();
		this.pattern = pattern;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public ScopeInsertSiblingDownAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final Scope newScope = new Scope("");
		newScopeId = newScope.getId();

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope) + 1, newScope);

		new ScopeUpdateAction(newScopeId, pattern).execute(context);
		return new ScopeRemoveAction(newScopeId);
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
