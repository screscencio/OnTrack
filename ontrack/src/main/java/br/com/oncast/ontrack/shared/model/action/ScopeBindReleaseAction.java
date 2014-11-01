package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindReleaseActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

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
		this.rollbackSubActions = new ArrayList<ModelAction>();
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
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);

		final Release oldRelease = selectedScope.getRelease();
		final int oldScopePriority = (oldRelease != null) ? oldRelease.removeScope(selectedScope) : -1;
		final String oldReleaseDescription = context.getReleaseDescriptionFor(oldRelease);

		final List<ModelAction> newRollbackSubActions = processRollbackSubActions(context, actionContext);
		if (shouldBindToNewRelease()) {
			final ModelAction releaseRemoveAction = assureNewReleaseExistence(context, actionContext);
			if (releaseRemoveAction != null) newRollbackSubActions.add(0, releaseRemoveAction);

			final Release newRelease = ActionHelper.findRelease(newReleaseDescription, context, this);
			if (newRelease.equals(oldRelease)) newRelease.addScope(selectedScope, oldScopePriority);
			else newRelease.addScope(selectedScope, scopePriority);

			final boolean notUndoing = newRollbackSubActions.size() == 1;
			if (!selectedScope.getEffort().hasInfered() && notUndoing) {
				final ScopeDeclareEffortAction scopeDeclareEffortAction = new ScopeDeclareEffortAction(selectedScope.getId(), true, 1);
				newRollbackSubActions.add(scopeDeclareEffortAction.execute(context, actionContext));
			}

			if (!selectedScope.getValue().hasInfered() && notUndoing) {
				final ScopeDeclareValueAction scopeDeclareValueAction = new ScopeDeclareValueAction(selectedScope.getId(), true, 1);
				newRollbackSubActions.add(scopeDeclareValueAction.execute(context, actionContext));
			}

			if (rollbackSubActionsContainsKanbanColumnCreateAction()) assureKanbanColumnExistence(selectedScope, newRelease, context);
		}
		newRollbackSubActions.addAll(processKanbanSubActions(context, actionContext));

		return new ScopeBindReleaseAction(referenceId, oldReleaseDescription, oldScopePriority, newRollbackSubActions);
	}

	private boolean rollbackSubActionsContainsKanbanColumnCreateAction() {
		for (final ModelAction action : rollbackSubActions) {
			if (action instanceof KanbanColumnCreateAction) return true;
		}
		return false;
	}

	private List<ModelAction> processKanbanSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> newRollbackSubActions = new ArrayList<ModelAction>();
		for (final ModelAction action : rollbackSubActions) {
			if (!(action instanceof KanbanColumnCreateAction)) continue;

			final ModelAction newRollbackAction = action.execute(context, actionContext);
			newRollbackSubActions.add(0, newRollbackAction);
		}
		return newRollbackSubActions;
	}

	private void assureKanbanColumnExistence(final Scope scope, final Release release, final ProjectContext context) {
		final Kanban kanban = context.getKanban(release);
		for (final Scope taks : scope.getAllLeafs()) {
			final String description = taks.getProgress().getDescription();
			if (!kanban.hasNonInferedColumn(description)) rollbackSubActions.add(new KanbanColumnCreateAction(release.getId(), description, false));
		}
	}

	private List<ModelAction> processRollbackSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> newRollbackSubActions = new ArrayList<ModelAction>();
		for (final ModelAction action : rollbackSubActions) {
			if (action instanceof KanbanColumnCreateAction) continue;

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
		} catch (final ReleaseNotFoundException e) {
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
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return true;
	}

	public boolean isUnbinding() {
		return newReleaseDescription == null || newReleaseDescription.isEmpty();
	}

	public String getNewReleaseDescription() {
		return newReleaseDescription;
	}
}