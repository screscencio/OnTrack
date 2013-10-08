package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.client.services.actionExecution.UndoWarningMessages;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeRemoveActionEntity.class)
public class ScopeRemoveAction implements ScopeAction, ShowsUndoAlertAfterActionExecution {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	public ScopeRemoveAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
	}

	public ScopeRemoveAction() {}

	@Override
	public ScopeRemoveRollbackAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_ROOT_NODE);

		final Scope parent = selectedScope.getParent();
		final UUID parentScopeId = parent.getId();
		final String description = selectedScope.getDescription();

		final List<ScopeRemoveRollbackAction> childActionRollbackList = executeChildActions(context, actionContext, selectedScope);
		final List<ModelAction> subActionRollbackList = executeSubActions(context, actionContext, selectedScope);

		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		// TODO Analyze the possibility of merging these two lists into only one sub-action list.
		return new ScopeRemoveRollbackAction(parentScopeId, referenceId, description, index, subActionRollbackList, childActionRollbackList);
	}

	private List<ScopeRemoveRollbackAction> executeChildActions(final ProjectContext context, final ActionContext actionContext, final Scope selectedScope) throws UnableToCompleteActionException {
		final List<ScopeRemoveRollbackAction> childActionRollbackList = new ArrayList<ScopeRemoveRollbackAction>();
		for (final Scope child : selectedScope.getChildren())
			childActionRollbackList.add(new ScopeRemoveAction(child.getId()).execute(context, actionContext));

		return childActionRollbackList;
	}

	private List<ModelAction> executeSubActions(final ProjectContext context, final ActionContext actionContext, final Scope selectedScope) throws UnableToCompleteActionException {
		final List<ModelAction> subActionList = new ArrayList<ModelAction>();

		for (final UserAssociationMetadata metadata : context.<UserAssociationMetadata> getMetadataList(selectedScope, UserAssociationMetadata.getType())) {
			subActionList.add(new ScopeRemoveAssociatedUserAction(referenceId, metadata.getUser().getId()));
		}

		for (final HumanIdMetadata metadata : context.<HumanIdMetadata> getMetadataList(selectedScope, HumanIdMetadata.getType())) {
			subActionList.add(new ScopeUnbindHumanIdAction(metadata));
		}

		subActionList.add(new ScopeDeclareProgressAction(referenceId, null));
		subActionList.add(new ScopeBindReleaseAction(referenceId, null));
		subActionList.add(new ScopeDeclareEffortAction(referenceId, false, 0));
		subActionList.add(new ScopeDeclareValueAction(referenceId, false, 0));

		for (final Annotation annotation : context.findAnnotationsFor(referenceId)) {
			subActionList.add(new AnnotationRemoveAction(referenceId, annotation.getId(), false));
		}

		for (final Checklist checklist : context.findChecklistsFor(referenceId)) {
			subActionList.add(new ChecklistRemoveAction(referenceId, checklist.getId()));
		}

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		for (final ModelAction subAction : subActionList) {
			subActionRollbackList.add(subAction.execute(context, actionContext));
		}
		return subActionRollbackList;
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final UUID referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

	@Override
	public String getAlertMessage(final UndoWarningMessages messages) {
		return messages.scopeRemove();
	}

}