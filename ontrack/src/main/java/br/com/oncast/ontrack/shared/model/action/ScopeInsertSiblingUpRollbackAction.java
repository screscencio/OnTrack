package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingUpRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeInsertSiblingUpRollbackActionEntity.class)
public class ScopeInsertSiblingUpRollbackAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("scopeUpdateRollbackAction")
	@Element
	private ScopeUpdateAction scopeUpdateRollbackAction;

	public ScopeInsertSiblingUpRollbackAction() {}

	public ScopeInsertSiblingUpRollbackAction(final UUID newScopeId, final ScopeUpdateAction scopeUpdateRollbackAction) {
		this.referenceId = newScopeId;
		this.scopeUpdateRollbackAction = scopeUpdateRollbackAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_ROOT_NODE);

		final Scope parent = selectedScope.getParent();
		final String pattern = new ScopeRepresentationBuilder(selectedScope).includeEverything().toString();
		final UUID siblingId = parent.getChild(parent.getChildIndex(selectedScope) + 1).getId();

		scopeUpdateRollbackAction.execute(context, actionContext);
		parent.remove(selectedScope);

		return new ScopeInsertSiblingUpAction(siblingId, referenceId, pattern);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	public ScopeUpdateAction getScopeUpdateRollbackAction() {
		return scopeUpdateRollbackAction;
	}

	public void setScopeUpdateRollbackAction(final ScopeUpdateAction scopeUpdateRollbackAction) {
		this.scopeUpdateRollbackAction = scopeUpdateRollbackAction;
	}

	public void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
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
