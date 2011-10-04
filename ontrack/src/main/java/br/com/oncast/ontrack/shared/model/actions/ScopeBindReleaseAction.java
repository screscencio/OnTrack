package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeBindReleaseActionEntity.class)
public class ScopeBindReleaseAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID referenceId;

	@ConversionAlias("newReleaseDescription")
	private String newReleaseDescription;

	@ConversionAlias("subAction")
	private ReleaseRemoveAction rollbackSubAction;

	@ConversionAlias("releaseCreateAction")
	private ReleaseCreateActionDefault releaseCreateAction;

	@ConversionAlias("scopePriority")
	private int scopePriority;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeBindReleaseAction() {}

	public ScopeBindReleaseAction(final UUID scopeId, final String newReleaseDescription) {
		this.referenceId = scopeId;
		this.newReleaseDescription = newReleaseDescription;
		this.scopePriority = -1;
	}

	public ScopeBindReleaseAction(final UUID scopeId, final String releaseDescription, final int scopePriority) {
		this(scopeId, releaseDescription);
		this.scopePriority = scopePriority;
	}

	public ScopeBindReleaseAction(final UUID scopeId, final String releaseDescription, final int scopePriority, final ReleaseRemoveAction subAction) {
		this(scopeId, releaseDescription, scopePriority);
		this.rollbackSubAction = subAction;
	}

	// TODO Reference a release by its ID, not by its description. (Think about the consequences).
	@Override
	public ScopeBindReleaseAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final Release oldRelease = selectedScope.getRelease();
		final int oldScopePriority = (oldRelease != null) ? oldRelease.removeScope(selectedScope) : -1;
		final String oldReleaseDescription = context.getReleaseDescriptionFor(oldRelease);

		if (rollbackSubAction != null) rollbackSubAction.execute(context);

		ReleaseRemoveAction newRollbackSubAction = null;
		if (newReleaseDescription != null && !newReleaseDescription.isEmpty()) {
			newRollbackSubAction = assureNewReleaseExistence(context);

			final Release newRelease = ReleaseActionHelper.findRelease(newReleaseDescription, context);
			if (newRelease.equals(oldRelease)) newRelease.addScope(selectedScope, oldScopePriority);
			else newRelease.addScope(selectedScope, scopePriority);
		}

		return new ScopeBindReleaseAction(referenceId, oldReleaseDescription, oldScopePriority, newRollbackSubAction);
	}

	private ReleaseRemoveAction assureNewReleaseExistence(final ProjectContext context) throws UnableToCompleteActionException {
		ReleaseRemoveAction newRollbackSubAction = null;
		try {
			context.findRelease(newReleaseDescription);
		}
		catch (final ReleaseNotFoundException e) {
			if (releaseCreateAction == null) releaseCreateAction = new ReleaseCreateActionDefault(newReleaseDescription);
			newRollbackSubAction = releaseCreateAction.execute(context);
		}
		return newRollbackSubAction;
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
	public boolean changesProgressInference() {
		return false;
	}
}