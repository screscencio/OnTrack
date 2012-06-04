package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveDownActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeMoveDownActionEntity.class)
public class ScopeMoveDownAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	public ScopeMoveDownAction(final UUID referenceId) {
		this.referenceId = referenceId;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveDownAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move the root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isLastNode(index, parent)) throw new UnableToCompleteActionException("It is not possible to move down the last node.");

		parent.remove(selectedScope);
		parent.add(index + 1, selectedScope);

		return new ScopeMoveUpAction(referenceId);
	}

	private boolean isLastNode(final int index, final Scope parent) {
		return parent.getChildren().size() - 1 == index;
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