/*
 * DECISION 2011-11-08 - During October, it was decided to save the timestamp of progress definitions changes.
 * Although it is necessary for features such as the "burn up", I - Lobo - do not like the choosen approach, that uses a time stamp inside this action to store
 * time changes. This should be reviewed because, in my opinion, this approach makes the "burn up" logic intrusive into the app model, demands huge effort to
 * control time stamp consistency problems between clients and is not very flexible (future logic updates may not have sufficient data).
 */

package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareProgressActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(ScopeDeclareProgressActionEntity.class)
public class ScopeDeclareProgressAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newProgressDescription")
	@Attribute
	private String newProgressDescription;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareProgressAction() {}

	public ScopeDeclareProgressAction(final UUID referenceId, final String newProgressDescription) {
		this(referenceId, newProgressDescription, new ArrayList<ModelAction>());
	}

	public ScopeDeclareProgressAction(final UUID referenceId, final String newProgressDescription, final List<ModelAction> rollbackActions) {
		this.uniqueId = new UUID();
		this.referenceId = referenceId;
		subActionList = rollbackActions;
		this.newProgressDescription = newProgressDescription == null ? "" : newProgressDescription;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		final String oldProgressDescription = selectedScope.getProgress().getDescription();

		selectedScope.getProgress().setDescription(newProgressDescription, ActionHelper.findActionAuthor(actionContext, context, this), actionContext.getTimestamp());
		final List<ModelAction> rollbackActions = processSubActions(context, actionContext, selectedScope);

		return new ScopeDeclareProgressAction(referenceId, oldProgressDescription, rollbackActions);
	}

	private List<ModelAction> processSubActions(final ProjectContext context, final ActionContext actionContext, final Scope scope) throws UnableToCompleteActionException {
		if (subActionList.isEmpty()) {
			checkUserAssociation(context, actionContext, scope);
			assureKanbanColumnExistence(context, scope);
		}

		return ActionHelper.executeSubActions(subActionList, context, actionContext);
	}

	private void checkUserAssociation(final ProjectContext context, final ActionContext actionContext, final Scope scope) {
		if (!ProgressState.UNDER_WORK.matches(newProgressDescription) || context.hasMetadata(scope, UserAssociationMetadata.getType())) return;

		subActionList.add(new ScopeAddAssociatedUserAction(referenceId, actionContext.getUserId()));
	}

	private void assureKanbanColumnExistence(final ProjectContext context, final Scope scope) throws UnableToCompleteActionException {
		final Scope story = scope.getStory();
		final Release release = story.getRelease();
		if (release == null) return;

		final Kanban kanban = context.getKanban(release);
		for (final Scope task : story.getAllLeafs()) {
			final String description = task.getProgress().getDescription();
			if (!kanban.hasNonInferedColumn(description)) subActionList.add(new KanbanColumnCreateAction(release.getId(), description, false));
		}
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
