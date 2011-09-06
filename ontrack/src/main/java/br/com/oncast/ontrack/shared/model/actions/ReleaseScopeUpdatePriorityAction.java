package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseScopeUpdatePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseScopeUpdatePriorityActionEntity.class)
public class ReleaseScopeUpdatePriorityAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("releaseReferenceId")
	private UUID releaseReferenceId;

	@ConversionAlias("scopeReferenceId")
	private UUID scopeReferenceId;

	@ConversionAlias("priority")
	private int priority;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseScopeUpdatePriorityAction() {}

	public ReleaseScopeUpdatePriorityAction(final UUID releaseReferenceId, final UUID scopeReferenceId, final int priority) {
		this.releaseReferenceId = releaseReferenceId;
		this.scopeReferenceId = scopeReferenceId;
		this.priority = priority;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release release = ReleaseActionHelper.findRelease(releaseReferenceId, context);
		final Scope scope = ScopeActionHelper.findScope(scopeReferenceId, context);

		if (!release.containsScope(scope)) throw new UnableToCompleteActionException(
				"The scope priority cannot be updated because it is not part of the referenced release.");
		if (priority < 0) throw new UnableToCompleteActionException(
				"The scope priority cannot be decreased because it already is the most prioritary in this release.");
		if (priority >= release.getScopeList().size()) throw new UnableToCompleteActionException(
				"The scope priority cannot be decreased because it already is the least prioritary in this release.");

		final int oldPriority = release.getScopeIndex(scope);
		release.removeScope(scope);
		release.addScope(scope, priority);

		return new ReleaseScopeUpdatePriorityAction(releaseReferenceId, scopeReferenceId, oldPriority);
	}

	@Override
	public UUID getReferenceId() {
		return releaseReferenceId;
	}
}
