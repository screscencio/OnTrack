package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveLeftActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(ScopeMoveLeftActionEntity.class)
public class ScopeMoveLeftAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

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
	protected ScopeMoveLeftAction() {}

	public ScopeMoveLeftAction(final UUID selectedScopeId) {
		this(selectedScopeId, new ArrayList<ModelAction>());
	}

	// TODO Analyze the possibility of replacing the sub-action list for a single typed action.
	public ScopeMoveLeftAction(final UUID selectedScopeId, final List<ModelAction> subActionList) {
		this.uniqueId = new UUID();
		this.referenceId = selectedScopeId;
		this.subActionList = subActionList;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.MOVE_ROOT_NODE);
		if (selectedScope.getParent().isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.MOVE_LEFT_ROOT_NODE_SON);

		final Scope parent = selectedScope.getParent();
		final Scope grandParent = parent.getParent();
		final int index = parent.getChildIndex(selectedScope);

		parent.remove(selectedScope);
		grandParent.add(grandParent.getChildIndex(parent) + 1, selectedScope);

		return new ScopeMoveRightAction(referenceId, index, executeSubActions(context, actionContext));
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

	private List<ModelAction> executeSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		for (final ModelAction subAction : subActionList) {
			subActionRollbackList.add(subAction.execute(context, actionContext));
		}
		return subActionRollbackList;
	}

}
