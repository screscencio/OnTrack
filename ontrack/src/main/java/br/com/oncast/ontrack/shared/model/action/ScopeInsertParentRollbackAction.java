package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeInsertParentRollbackActionEntity.class)
public class ScopeInsertParentRollbackAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newScopeId")
	@Element
	private UUID newScopeId;

	@ConversionAlias("scopeUpdateAction")
	@Element
	private ScopeUpdateAction scopeUpdateAction;

	public ScopeInsertParentRollbackAction(final UUID newScopeId, final UUID selectedScopeId, final ScopeUpdateAction updateAction) {
		this.newScopeId = newScopeId;
		this.referenceId = selectedScopeId;
		this.scopeUpdateAction = updateAction;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertParentRollbackAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope newScope = ScopeActionHelper.findScope(newScopeId, context);
		if (newScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to remove a root node.");
		if (newScope.getChildren().size() <= 0) throw new UnableToCompleteActionException("It is not possible to rollback this action due to inconsistences.");

		final String pattern = new ScopeRepresentationBuilder(newScope).includeEverything().toString();

		scopeUpdateAction.execute(context);

		final Scope child = newScope.getChildren().get(0);
		final Scope parent = newScope.getParent();
		final int index = parent.getChildIndex(newScope);
		parent.remove(newScope);

		newScope.clearChildren();
		parent.add(index, child);

		return new ScopeInsertParentAction(referenceId, newScopeId, pattern);
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
