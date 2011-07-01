package br.com.oncast.ontrack.shared.model.scope.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUpdateRollbackActionEntity;
import br.com.oncast.ontrack.server.util.converter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.util.converter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeUpdateRollbackActionEntity.class)
public class ScopeUpdateRollbackAction implements ScopeAction {

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newPattern")
	private String newPattern;

	@ConversionAlias("oldDescription")
	private String oldDescription;

	@ConversionAlias("oldReleaseDescription")
	private String oldReleaseDescription;

	public ScopeUpdateRollbackAction(final UUID selectedScopeId, final String newPattern, final String oldDescription, final String oldReleaseDescription) {
		this.referenceId = selectedScopeId;
		this.newPattern = newPattern;
		this.oldDescription = oldDescription;
		this.oldReleaseDescription = oldReleaseDescription;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeUpdateRollbackAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		if (oldDescription == null) throw new UnableToCompleteActionException("The action cannot be rolled back because it has never been executed.");

		final Scope selectedScope = context.findScope(referenceId);
		final Release newRelease = selectedScope.getRelease();
		if (newRelease != null) newRelease.removeScope(selectedScope);

		selectedScope.setDescription(oldDescription);
		final Release oldRelease = context.loadRelease(oldReleaseDescription);
		selectedScope.setRelease(oldRelease);
		if (oldRelease != null) oldRelease.addScope(selectedScope);

		return new ScopeUpdateAction(referenceId, newPattern);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

}
