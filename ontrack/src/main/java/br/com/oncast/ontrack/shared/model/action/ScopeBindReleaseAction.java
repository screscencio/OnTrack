package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(ScopeBindReleaseActionEntity.class)
public class ScopeBindReleaseAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newReleaseDescription")
	@Attribute(required = false)
	private String newReleaseDescription;

	@ConversionAlias("subActionList")
	@IgnoredByDeepEquality
	@ElementList(required = false)
	private List<ModelAction> rollbackSubActions;

	@ConversionAlias("releaseCreateAction")
	@Element(required = false)
	@IgnoredByDeepEquality
	private ModelAction releaseCreateAction;

	@ConversionAlias("scopePriority")
	@Attribute
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

	public ScopeBindReleaseAction(final UUID scopeId, final String releaseDescription, final int scopePriority, final List<ModelAction> subActions) {
		this(scopeId, releaseDescription, scopePriority);
		this.rollbackSubActions = subActions;
	}

	// TODO Reference a release by its ID, not by its description. (Think about the consequences).
	@Override
	public ScopeBindReleaseAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context);

		final Release oldRelease = selectedScope.getRelease();
		final int oldScopePriority = (oldRelease != null) ? oldRelease.removeScope(selectedScope) : -1;
		final String oldReleaseDescription = context.getReleaseDescriptionFor(oldRelease);

		final List<ModelAction> newRollbackSubActions = (rollbackSubActions == null) ? new ArrayList<ModelAction>() : processRollbackActions(context,
				actionContext);

		if (shouldBindToNewRelease()) {
			final ModelAction releaseExistenceAssuranceAction = assureNewReleaseExistence(context, actionContext);
			if (releaseExistenceAssuranceAction != null) newRollbackSubActions.add(0, releaseExistenceAssuranceAction);

			final Release newRelease = ActionHelper.findRelease(newReleaseDescription, context);
			if (newRelease.equals(oldRelease)) newRelease.addScope(selectedScope, oldScopePriority);
			else newRelease.addScope(selectedScope, scopePriority);

			if (selectedScope.getProgress().getState().equals(ProgressState.UNDER_WORK)) newRollbackSubActions.add(new ScopeDeclareProgressAction(referenceId,
					selectedScope.getProgress().getDescription()).execute(context, actionContext));
		}

		return new ScopeBindReleaseAction(referenceId, oldReleaseDescription, oldScopePriority, newRollbackSubActions);
	}

	private List<ModelAction> processRollbackActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> newRollbackSubActions = new ArrayList<ModelAction>();
		for (final ModelAction action : rollbackSubActions) {
			final ModelAction newRollbackAction = action.execute(context, actionContext);
			newRollbackSubActions.add(0, newRollbackAction);

			if (newRollbackAction instanceof ReleaseCreateAction) releaseCreateAction = newRollbackAction;
		}
		return newRollbackSubActions;
	}

	private boolean shouldBindToNewRelease() {
		return newReleaseDescription != null && !new ReleaseDescriptionParser(newReleaseDescription).getHeadRelease().isEmpty();
	}

	private ModelAction assureNewReleaseExistence(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		ModelAction newRollbackSubAction = null;
		try {
			context.findRelease(newReleaseDescription);
		}
		catch (final ReleaseNotFoundException e) {
			if (releaseCreateAction == null) releaseCreateAction = new ReleaseCreateAction(newReleaseDescription);
			newRollbackSubAction = releaseCreateAction.execute(context, actionContext);
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

	@Override
	public boolean changesValueInference() {
		return false;
	}

	public boolean isUnbinding() {
		return newReleaseDescription == null || newReleaseDescription.isEmpty();
	}
}