package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveRightActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeMoveRightActionEntity.class)
public class ScopeMoveRightAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("position")
	@Attribute
	private int position;

	@ConversionAlias("wasIndexSet")
	@Attribute
	private boolean wasIndexSet;

	@ConversionAlias("subActionList")
	@ElementList
	private List<ModelAction> subActionList;

	public ScopeMoveRightAction(final UUID selectedScopeId) {
		this.referenceId = selectedScopeId;
		this.wasIndexSet = false;
		this.position = -1;
		this.subActionList = new ArrayList<ModelAction>();
	}

	// TODO Analyze the possibility of replacing the sub-action list for a single typed action.
	public ScopeMoveRightAction(final UUID selectedScopeId, final int position, final List<ModelAction> subActionList) {
		this.referenceId = selectedScopeId;
		this.position = position;
		this.wasIndexSet = true;
		this.subActionList = subActionList;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeMoveRightAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context);
		if (selectedScope.isRoot()) throw new UnableToCompleteActionException("It is not possible to move the root node.");

		final Scope parent = selectedScope.getParent();
		if (isFirstNode(parent.getChildIndex(selectedScope))) throw new UnableToCompleteActionException(
				"It's not possible to move right the first node");

		final List<ModelAction> subActionRollbackList = new ArrayList<ModelAction>();

		final Scope upperSibling = parent.getChildren().get(parent.getChildIndex(selectedScope) - 1);
		if (upperSibling.isLeaf()) subActionRollbackList.add(removeDeclaredProgress(upperSibling, context));

		selectedScope.getParent().remove(selectedScope);
		if (wasIndexSet) upperSibling.add(position, selectedScope);
		else upperSibling.add(selectedScope);

		return new ScopeMoveLeftAction(referenceId, subActionRollbackList);
	}

	private ModelAction removeDeclaredProgress(final Scope scope, final ProjectContext context) throws UnableToCompleteActionException {
		final ScopeDeclareProgressAction removeProgressAction = new ScopeDeclareProgressAction(scope.getId(), "");
		subActionList.add(removeProgressAction);

		return removeProgressAction.execute(context);
	}

	private boolean isFirstNode(final int index) {
		return index == 0;
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
}