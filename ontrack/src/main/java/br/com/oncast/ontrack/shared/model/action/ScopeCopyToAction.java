package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeCopyToActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Effort;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Value;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findDescription;
import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findScope;
import static br.com.oncast.ontrack.shared.model.action.helper.ActionHelper.findUser;

@ConvertTo(ScopeCopyToActionEntity.class)
public class ScopeCopyToAction implements ScopeInsertAction, HasDestination {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID sourceScopeId;

	@Element
	private UUID targetedParentId;

	@Element
	private int targetIndex;

	@Element
	private UUID newScopeId;

	@ElementList(required = false)
	private List<ModelAction> subActionList;

	protected ScopeCopyToAction() {}

	public ScopeCopyToAction(final UUID sourceScopeId) {
		this.sourceScopeId = sourceScopeId;
		this.newScopeId = new UUID();
	}

	public ScopeCopyToAction(final UUID sourceScopeId, final UUID desiredParentScopeId, final int desiredIndex) {
		this(sourceScopeId);
		setDestination(desiredParentScopeId, desiredIndex);
	}

	@Override
	public ScopeCopyToAction setDestination(final UUID desiredParentScopeId, final int desiredIndex) {
		this.targetedParentId = desiredParentScopeId;
		this.targetIndex = desiredIndex;
		return this;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		addNewScope(context, actionContext);

		saveSourceAttributes(context);

		for (final ModelAction action : subActionList) {
			action.execute(context, actionContext);
		}

		return new ScopeRemoveAction(newScopeId);
	}

	private void addNewScope(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope targetedParent = findScope(targetedParentId, context, this);
		final UserRepresentation author = findUser(actionContext.getUserId(), context, this);
		targetedParent.add(targetIndex, new Scope("", newScopeId, author, actionContext.getTimestamp()));
	}

	public ScopeCopyToAction saveSourceAttributes(final ProjectContext context) throws UnableToCompleteActionException {
		if (subActionList != null) return this;
		subActionList = new ArrayList<ModelAction>();

		final Scope source = findScope(sourceScopeId, context, this);

		subActionList.add(new ScopeUpdateAction(newScopeId, source.getDescription()));

		final Effort effort = source.getEffort();
		subActionList.add(new ScopeDeclareEffortAction(newScopeId, effort.hasDeclared(), effort.getDeclared()));

		final Value value = source.getValue();
		subActionList.add(new ScopeDeclareValueAction(newScopeId, value.hasDeclared(), value.getDeclared()));

		if (context.hasDescriptionFor(sourceScopeId)) {
			final Description description = findDescription(sourceScopeId, context, this);
			subActionList.add(new DescriptionCreateAction(newScopeId, description.getDescription()));
		}

		for (final Checklist cl : context.findChecklistsFor(sourceScopeId)) {
			final ChecklistCreateAction action = new ChecklistCreateAction(newScopeId, cl.getTitle());
			subActionList.add(action);
			for (final ChecklistItem item : cl.getItems()) {
				subActionList.add(new ChecklistAddItemAction(newScopeId, action.getChecklistId(), item.getDescription()));
			}
		}

		for (final Tag tag : context.getTagsFor(source)) {
			subActionList.add(new ScopeAddTagAssociationAction(newScopeId, tag.getId()));
		}

		for (final Scope child : source.getChildren()) {
			subActionList.add(new ScopeCopyToAction(child.getId(), newScopeId, source.getChildIndex(child)).saveSourceAttributes(context));
		}

		return this;
	}

	@Override
	public UUID getNewScopeId() {
		return newScopeId;
	}

	@Override
	public UUID getReferenceId() {
		return targetedParentId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

	@Override
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public UUID getSourceScopeId() {
		return sourceScopeId;
	}

	public void setSourceScopeId(final UUID sourceScopeId) {
		this.sourceScopeId = sourceScopeId;
	}

	public UUID getTargetedParentId() {
		return targetedParentId;
	}

	public void setTargetedParentId(final UUID targetedParentId) {
		this.targetedParentId = targetedParentId;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(final int targetIndex) {
		this.targetIndex = targetIndex;
	}

	public List<ModelAction> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelAction> subActionList) {
		this.subActionList = subActionList;
	}

	public void setNewScopeId(final UUID newScopeId) {
		this.newScopeId = newScopeId;
	}

}
