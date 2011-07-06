package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareEffortActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDeclareEffortActionEntity.class)
public class ScopeDeclareEffortAction implements ModelAction {

	@ConversionAlias("referenceId")
	private final UUID referenceId;

	@ConversionAlias("hasDeclaredEffort")
	private final boolean hasDeclaredEffort;

	@ConversionAlias("newDeclaredEffort")
	private final int newDeclaredEffort;

	public ScopeDeclareEffortAction(final UUID referenceId, final boolean hasDeclaredEffort, final int newDeclaredEffort) {
		this.referenceId = referenceId;
		this.hasDeclaredEffort = hasDeclaredEffort;
		this.newDeclaredEffort = newDeclaredEffort;
	}

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

}
