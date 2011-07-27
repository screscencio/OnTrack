package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeProgressActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeProgressActionEntity.class)
public class ScopeProgressAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newProgressDescription")
	private String newProgressDescription;

	public ScopeProgressAction(final UUID referenceId, final String newProgressDescription) {
		this.referenceId = referenceId;
		this.newProgressDescription = newProgressDescription;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeProgressAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);
		final String oldProgressDescription = selectedScope.getProgress().getDescription();

		if (newProgressDescription != null && !newProgressDescription.isEmpty()) selectedScope.getProgress().setDescription(newProgressDescription);
		else selectedScope.getProgress().reset();

		return new ScopeProgressAction(referenceId, oldProgressDescription);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}
}
