package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertParentActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeInsertParentActionEntity.class)
public class ScopeInsertParentAction implements ScopeInsertAction {

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

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertParentAction() {}

	public ScopeInsertParentAction(final UUID selectedScopeId, final String pattern) {
		this(selectedScopeId, new UUID(), pattern);
	}

	public ScopeInsertParentAction(final UUID selectedScopeId, final UUID newScopeId, final String pattern) {
		this.referenceId = selectedScopeId;
		this.newScopeId = newScopeId;
		scopeUpdateAction = new ScopeUpdateAction(newScopeId, pattern);
	}

	@Override
	public ScopeInsertParentRollbackAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CREATE_ROOT_PARENT);

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		final Scope newScope = new Scope("", newScopeId, ActionHelper.findActionAuthor(actionContext, context, this), actionContext.getTimestamp());

		parent.add(index, newScope);
		newScope.add(selectedScope);

		final ScopeUpdateAction updateAction = scopeUpdateAction.execute(context, actionContext);
		return new ScopeInsertParentRollbackAction(newScopeId, referenceId, updateAction);
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

	@Override
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}
}