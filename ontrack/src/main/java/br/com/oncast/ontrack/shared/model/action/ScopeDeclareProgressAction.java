/*
 * DECISION 2011-11-08 - During October, it was decided to save the timestamp of progress definitions changes.
 * Although it is necessary for features such as the "burn up", I - Lobo - do not like the choosen approach, that uses a time stamp inside this action to store
 * time changes. This should be reviewed because, in my opinion, this approach makes the "burn up" logic intrusive into the app model, demands huge effort to
 * control time stamp consistency problems between clients and is not very flexible (future logic updates may not have sufficient data).
 */

package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareProgressActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(ScopeDeclareProgressActionEntity.class)
public class ScopeDeclareProgressAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newProgressDescription")
	@Attribute
	private String newProgressDescription;

	@ConversionAlias("subAction")
	@Element(required = false)
	@IgnoredByDeepEquality
	private ModelAction rollbackSubAction;

	public ScopeDeclareProgressAction(final UUID referenceId, final String newProgressDescription) {
		this.referenceId = referenceId;
		this.newProgressDescription = newProgressDescription == null ? "" : newProgressDescription;
	}

	public ScopeDeclareProgressAction(final UUID referenceId, final String newProgressDescription, final ModelAction rollbackAction) {
		this.referenceId = referenceId;
		rollbackSubAction = rollbackAction;
		this.newProgressDescription = newProgressDescription == null ? "" : newProgressDescription;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareProgressAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context);
		final String oldProgressDescription = selectedScope.getProgress().getDescription();

		final ModelAction rollback = processSubActions(context, actionContext, selectedScope);
		selectedScope.getProgress().setDescription(newProgressDescription, ActionHelper.findUserFrom(actionContext, context), actionContext.getTimestamp());

		return new ScopeDeclareProgressAction(referenceId, oldProgressDescription, rollback);
	}

	private ModelAction processSubActions(final ProjectContext context, final ActionContext actionContext, final Scope scope)
			throws UnableToCompleteActionException {
		return (rollbackSubAction != null) ? rollbackSubAction.execute(context, actionContext) : assureKanbanColumnExistence(context, actionContext,
				scope);
	}

	private ModelAction assureKanbanColumnExistence(final ProjectContext context, final ActionContext actionContext, final Scope scope)
			throws UnableToCompleteActionException {
		if (!scope.isLeaf()) return null;

		final Release release = getScopeRelease(scope);
		if (release == null || context.getKanban(release).hasNonInferedColumn(newProgressDescription)) return null;

		return (new KanbanColumnCreateAction(release.getId(), newProgressDescription, false)).execute(context, actionContext);
	}

	private Release getScopeRelease(final Scope scope) {
		Scope currentScope = scope;
		Release release = scope.getRelease();
		while (release == null && !currentScope.isRoot()) {
			currentScope = currentScope.getParent();
			release = currentScope.getRelease();
		}
		return release;
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
		return true;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}

}
