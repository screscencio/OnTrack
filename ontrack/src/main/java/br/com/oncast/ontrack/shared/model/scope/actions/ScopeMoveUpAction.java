package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.annotations.MapTo;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveUpActionEntity;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@MapTo(ScopeMoveUpActionEntity.class)
public class ScopeMoveUpAction implements ScopeMoveAction {

	private UUID referenceId;

	public ScopeMoveUpAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveUpAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");

		final Scope parent = selectedScope.getParent();
		final int index = parent.getChildIndex(selectedScope);

		if (isFirstNode(index)) throw new UnableToCompleteActionException("It is not possible to move up the first node.");

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
}