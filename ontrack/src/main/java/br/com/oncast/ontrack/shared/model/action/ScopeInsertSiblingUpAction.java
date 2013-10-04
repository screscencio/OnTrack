package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingUpActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeInsertSiblingUpActionEntity.class)
public class ScopeInsertSiblingUpAction implements ScopeInsertSiblingAction {

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

	public ScopeInsertSiblingUpAction() {}

	public ScopeInsertSiblingUpAction(final UUID selectedScopeId, final String pattern) {
		this(selectedScopeId, new UUID(), pattern);
	}

	public ScopeInsertSiblingUpAction(final UUID referenceId, final UUID newScopeId, final String pattern) {
		this.referenceId = referenceId;
		this.newScopeId = newScopeId;
		scopeUpdateAction = new ScopeUpdateAction(newScopeId, pattern);
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CREATE_ROOT_SIBLING);

		final Scope newScope = new Scope("", newScopeId, ActionHelper.findUserFrom(actionContext, context, this), actionContext.getTimestamp());

		final Scope parent = selectedScope.getParent();
		parent.add(parent.getChildIndex(selectedScope), newScope);

		final ScopeUpdateAction updateRollbackAction = scopeUpdateAction.execute(context, actionContext);
		return new ScopeInsertSiblingUpRollbackAction(newScopeId, updateRollbackAction);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}

	public ScopeUpdateAction getScopeUpdateAction() {
		return scopeUpdateAction;
	}

	public void setScopeUpdateAction(final ScopeUpdateAction scopeUpdateAction) {
		this.scopeUpdateAction = scopeUpdateAction;
	}

	public void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
	}

	public void setNewScopeId(final UUID newScopeId) {
		this.newScopeId = newScopeId;
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
