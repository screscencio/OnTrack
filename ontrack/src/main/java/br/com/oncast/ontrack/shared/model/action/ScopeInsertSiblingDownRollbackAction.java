package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeInsertSiblingDownRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.ScopeRepresentationBuilder;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeInsertSiblingDownRollbackActionEntity.class)
public class ScopeInsertSiblingDownRollbackAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("scopeUpdateRollbackAction")
	@Element
	private ScopeUpdateAction scopeUpdateRollbackAction;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeInsertSiblingDownRollbackAction() {}

	public ScopeInsertSiblingDownRollbackAction(final UUID newScopeId, final ScopeUpdateAction scopeUpdateRollbackAction) {
		this.uniqueId = new UUID();
		this.referenceId = newScopeId;
		this.scopeUpdateRollbackAction = scopeUpdateRollbackAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_ROOT_NODE);

		final Scope parent = selectedScope.getParent();
		final String pattern = new ScopeRepresentationBuilder(selectedScope).includeEverything().toString();
		final UUID siblingId = parent.getChild(parent.getChildIndex(selectedScope) - 1).getId();

		scopeUpdateRollbackAction.execute(context, actionContext);
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
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

}
