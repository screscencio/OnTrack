package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeRemoveActionEntity.class)
public class ScopeRemoveAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

	public ScopeRemoveAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
		this.subActionList = new ArrayList<ModelAction>();
	}

	public ScopeRemoveAction(final UUID selectedScopeId, final List<ModelAction> subActionList) {
		this.referenceId = selectedScopeId;
		this.subActionList = subActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeRemoveAction() {}

	@Override
	public ScopeRemoveRollbackAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("Unable to remove root level.");

		final Scope parent = selectedScope.getParent();
		final UUID parentScopeId = parent.getId();
		final String description = selectedScope.getDescription();

		final List<ScopeRemoveRollbackAction> childActionRollbackList = executeChildActions(context, selectedScope);
		final List<ModelAction> subActionRollbackList = executeSubActions(context, selectedScope);

		final int index = parent.getChildIndex(selectedScope);
		parent.remove(selectedScope);

		// TODO Analyze the possibility of merging these two lists into only one sub-action list.
		return new ScopeRemoveRollbackAction(parentScopeId, referenceId, description, index, subActionRollbackList, childActionRollbackList);
	}

	private List<ScopeRemoveRollbackAction> executeChildActions(final ProjectContext context, final Scope selectedScope) throws UnableToCompleteActionException {
		final List<ScopeRemoveRollbackAction> childActionRollbackList = new ArrayList<ScopeRemoveRollbackAction>();
		for (final Scope child : selectedScope.getChildren())
			childActionRollbackList.add(new ScopeRemoveAction(child.getId()).execute(context));

		return childActionRollbackList;
	}

	private List<ModelAction> executeSubActions(final ProjectContext context, final Scope selectedScope) throws UnableToCompleteActionException {
		subActionList.add(new ScopeDeclareProgressAction(referenceId, null));
		subActionList.add(new ScopeBindReleaseAction(referenceId, null));
		subActionList.add(new ScopeDeclareEffortAction(referenceId, false, 0));

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		for (final ModelAction subAction : subActionList) {
			subActionRollbackList.add(subAction.execute(context));
		}
		return subActionRollbackList;
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
		return true;
	}
}