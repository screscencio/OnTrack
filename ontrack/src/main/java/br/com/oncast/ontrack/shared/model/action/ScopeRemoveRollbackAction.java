package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(ScopeRemoveRollbackActionEntity.class)
public class ScopeRemoveRollbackAction implements ScopeInsertAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("parentScopeId")
	@Element
	private UUID parentScopeId;

	@ConversionAlias("index")
	@Attribute
	private int index;

	@ConversionAlias("description")
	@Attribute
	private String description;

	@ConversionAlias("childActionList")
	@ElementList
	private List<ScopeRemoveRollbackAction> childActionList;

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
	protected ScopeRemoveRollbackAction() {}

	public ScopeRemoveRollbackAction(final UUID parentScopeId, final UUID selectedScopeId, final String description, final int index, final List<ModelAction> subActionList,
			final List<ScopeRemoveRollbackAction> childActionList) {
		this.uniqueId = new UUID();
		this.parentScopeId = parentScopeId;
		this.referenceId = selectedScopeId;
		this.index = index;
		this.description = description;
		this.subActionList = subActionList;
		this.childActionList = childActionList;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope parent = ActionHelper.findScope(parentScopeId, context, this);
		final Scope newScope = new Scope(description, referenceId, ActionHelper.findActionAuthor(actionContext, context, this), actionContext.getTimestamp());

		parent.add(index, newScope);

		executSubActions(context, actionContext);
		executeChildActions(context, actionContext);

		return new ScopeRemoveAction(referenceId);
	}

	private void executeChildActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		for (int i = childActionList.size() - 1; i >= 0; i--) {
			childActionList.get(i).execute(context, actionContext);
		}
	}

	private void executSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		for (final ModelAction subAction : subActionList)
			subAction.execute(context, actionContext);
	}

	@Override
	public UUID getReferenceId() {
		return parentScopeId;
	}

	@Override
	public UUID getNewScopeId() {
		return referenceId;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesEffortInference() {
		return true;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesValueInference() {
		return true;
	}

	// TODO Result should depend on its subActions.
	@Override
	public boolean changesProgressInference() {
		return true;
	}
}
