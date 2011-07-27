package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareEffortActionEntity;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDeclareEffortActionEntity.class)
public class ScopeDeclareEffortAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("hasDeclaredEffort")
	private boolean hasDeclaredEffort;

	@ConversionAlias("newDeclaredEffort")
	private int newDeclaredEffort;

	public ScopeDeclareEffortAction(final UUID referenceId, final boolean hasDeclaredEffort, final int newDeclaredEffort) {
		this.referenceId = referenceId;
		this.hasDeclaredEffort = hasDeclaredEffort;
		this.newDeclaredEffort = newDeclaredEffort;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareEffortAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);

		final boolean hadDeclared = selectedScope.getEffort().hasDeclared();
		final int oldDeclaredEffort = selectedScope.getEffort().getDeclared();

		if (hasDeclaredEffort) selectedScope.getEffort().setDeclared(newDeclaredEffort);
		else selectedScope.getEffort().resetDeclared();

		return new ScopeDeclareEffortAction(referenceId, hadDeclared, oldDeclaredEffort);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}
}
