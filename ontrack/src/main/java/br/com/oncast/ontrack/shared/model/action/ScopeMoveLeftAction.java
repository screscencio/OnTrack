package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveLeftActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeMoveLeftActionEntity.class)
public class ScopeMoveLeftAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

	public ScopeMoveLeftAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
		this.subActionList = new ArrayList<ModelAction>();
	}

	// TODO Analyze the possibility of replacing the sub-action list for a single typed action.
	public ScopeMoveLeftAction(final UUID selectedScopeId, final List<ModelAction> subActionList) {
		this.referenceId = selectedScopeId;
		this.subActionList = subActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveLeftAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move a root node.");
		if (selectedScope.getParent().isRoot()) throw new UnableToCompleteActionException("It is not possible to move left when a node parent is a root node.");

		final Scope parent = selectedScope.getParent();
		final Scope grandParent = parent.getParent();
		final int index = parent.getChildIndex(selectedScope);

		parent.remove(selectedScope);
		grandParent.add(grandParent.getChildIndex(parent) + 1, selectedScope);

		return new ScopeMoveRightAction(referenceId, index, executeSubActions(context));
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
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

	private List<ModelAction> executeSubActions(final ProjectContext context) throws UnableToCompleteActionException {
		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();
		for (final ModelAction subAction : subActionList) {
			subActionRollbackList.add(subAction.execute(context));
		}
		return subActionRollbackList;
	}

}
