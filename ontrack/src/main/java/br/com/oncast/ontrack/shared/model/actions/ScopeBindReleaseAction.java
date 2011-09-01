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
	private ReleaseAction rollbackSubAction;

	@ConversionAlias("releaseCreateAction")
	private ReleaseCreateActionDefault releaseCreateAction;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeBindReleaseAction() {}

	public ScopeBindReleaseAction(final UUID referenceId, final String newReleaseDescription) {
		this.referenceId = referenceId;
		this.newReleaseDescription = newReleaseDescription;
	}

	// Used by this action itself when creating a rollback action.
	public ScopeBindReleaseAction(final UUID referenceId, final String releaseDescription, final ReleaseRemoveAction subAction) {
		this(referenceId, releaseDescription);
		this.rollbackSubAction = subAction;
	}

	// TODO Reference a release by its ID, not by its description. (Think about the consequences).
	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final Release oldRelease = dissociateCurrentRelease(selectedScope, context);
		executeRollbackSubActions(context);

		ReleaseRemoveAction newRollbackSubAction = null;
		if (newReleaseDescription != null && !newReleaseDescription.isEmpty()) {
			newRollbackSubAction = assureNewReleaseExistence(context);
			associateNewRelease(context, selectedScope);
		}

		return new ScopeBindReleaseAction(referenceId, context.getReleaseDescriptionFor(oldRelease), newRollbackSubAction);
	}

	private void associateNewRelease(final ProjectContext context, final Scope selectedScope) throws UnableToCompleteActionException {
		final Release newRelease = ReleaseActionHelper.loadRelease(newReleaseDescription, context);
		newRelease.addScope(selectedScope);
	}

	private Release dissociateCurrentRelease(final Scope selectedScope, final ProjectContext context) throws UnableToCompleteActionException {
		final Release oldRelease = selectedScope.getRelease();
		if (oldRelease != null) oldRelease.removeScope(selectedScope);
		return oldRelease;
	}

	private void executeRollbackSubActions(final ProjectContext context) throws UnableToCompleteActionException {
		if (rollbackSubAction != null) rollbackSubAction.execute(context);
	}

	private ReleaseRemoveAction assureNewReleaseExistence(final ProjectContext context) throws UnableToCompleteActionException {
		ReleaseRemoveAction newRollbackSubAction = null;
		try {
			context.loadRelease(newReleaseDescription);
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