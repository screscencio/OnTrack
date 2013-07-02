package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
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
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope newScope = ActionHelper.findScope(newScopeId, context, this);
		if (newScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_ROOT_NODE);
		if (newScope.getChildren().size() <= 0) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ROLLBACK_INCONSITENCY);

		final String pattern = new ScopeRepresentationBuilder(newScope).includeEverything().toString();

		scopeUpdateAction.execute(context, actionContext);

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
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}
}
