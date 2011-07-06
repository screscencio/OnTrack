package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeBindReleaseActionEntity.class)
public class ScopeBindReleaseAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newReleaseDescription")
	private String newReleaseDescription;

	public ScopeBindReleaseAction(final UUID referenceId, final String newReleaseDescription) {
		this.referenceId = referenceId;
		this.newReleaseDescription = newReleaseDescription;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeBindReleaseAction() {}

	// TODO Reference a release by its ID, not by its description. (Think about the consequences).
	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = context.findScope(referenceId);

		final Release oldRelease = selectedScope.getRelease();
		final String oldReleaseDescription = context.getReleaseDescriptionFor(oldRelease);
		if (oldRelease != null) oldRelease.removeScope(selectedScope);

		final Release newRelease = context.loadRelease(newReleaseDescription);
		selectedScope.setRelease(newRelease);
		if (newRelease != null) newRelease.addScope(selectedScope);

		return new ScopeBindReleaseAction(referenceId, oldReleaseDescription);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

}
