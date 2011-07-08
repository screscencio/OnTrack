package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingUpActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertSiblingUpActionEntity.class)
public class ScopeInsertSiblingUpAction implements ScopeInsertSiblingAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newScopeId")
	private UUID newScopeId;

	@ConversionAlias("pattern")
	private String pattern;

	public ScopeInsertSiblingUpAction(final UUID selectedScopeId, final String pattern) {
		this.referenceId = selectedScopeId;
		this.pattern = pattern;
		this.newScopeId = new UUID();
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertSiblingUpAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");

		final Scope newScope = new Scope("", newScopeId);

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope), newScope);

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

	@Override
	public boolean changesEffortInference() {
		return true;
	}
}
