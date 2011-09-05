package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ScopeDecreasePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDecreasePriorityActionEntity.class)
public class ScopeDecreasePriorityAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("releaseReferenceId")
	private UUID releaseReferenceId;

	@ConversionAlias("scopeReferenceId")
	private UUID scopeReferenceId;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDecreasePriorityAction() {}

	public ScopeDecreasePriorityAction(final UUID releaseReferenceId, final UUID scopeReferenceId) {
		this.releaseReferenceId = releaseReferenceId;
		this.scopeReferenceId = scopeReferenceId;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release release = ReleaseActionHelper.findRelease(releaseReferenceId, context);
		final Scope scope = ScopeActionHelper.findScope(scopeReferenceId, context);

		final int index = release.getScopeIndex(scope);
		if (index < 0) throw new UnableToCompleteActionException("The scope priority cannot be updated because it is not part of the referenced release.");
		if (index >= release.getScopeList().size() - 1) throw new UnableToCompleteActionException(
				"The scope priority cannot be decreased because it already is the least prioritary in this release.");
		release.removeScope(scope);
		release.addScope(scope, index + 1);

		return new ScopeIncreasePriorityAction(releaseReferenceId, scopeReferenceId);
	}

	@Override
	public UUID getReferenceId() {
		return releaseReferenceId;
	}
}
