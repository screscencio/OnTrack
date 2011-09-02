package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingDownRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertSiblingDownRollbackActionEntity.class)
public class ScopeInsertSiblingDownRollbackAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("scopeUpdateRollbackAction")
	private ScopeUpdateAction scopeUpdateRollbackAction;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertSiblingDownRollbackAction() {}

	public ScopeInsertSiblingDownRollbackAction(final UUID newScopeId, final ScopeUpdateAction scopeUpdateRollbackAction) {
		this.referenceId = newScopeId;
		this.scopeUpdateRollbackAction = scopeUpdateRollbackAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("Unable to remove root level.");

		final Scope parent = selectedScope.getParent();
		final String pattern = new ScopeRepresentationBuilder(selectedScope).includeEverything().toString();
		final UUID siblingId = parent.getChild(parent.getChildIndex(selectedScope) - 1).getId();

		scopeUpdateRollbackAction.execute(context);
		parent.remove(selectedScope);

		return new ScopeInsertSiblingDownAction(siblingId, referenceId, pattern);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

}
