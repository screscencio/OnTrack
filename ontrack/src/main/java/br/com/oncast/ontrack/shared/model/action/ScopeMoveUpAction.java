package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveUpActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeMoveUpActionEntity.class)
public class ScopeMoveUpAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

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

	protected ScopeMoveUpAction() {}

	public ScopeMoveUpAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
		this.uniqueId = new UUID();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.MOVE_ROOT_NODE);

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.MOVE_UP_FIRST_NODE);

		parent.remove(selectedScope);
		parent.add(index - 1, selectedScope);

		return new ScopeMoveDownAction(referenceId);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}
}