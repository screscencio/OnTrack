package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRemoveRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(ReleaseRemoveRollbackActionEntity.class)
public class ReleaseRemoveRollbackAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID newReleaseId;

	@ConversionAlias("parentReleaseId")
	@Element
	private UUID parentReleaseId;

	@ConversionAlias("description")
	@Attribute
	private String description;

	@ConversionAlias("index")
	@Attribute
	private int index;

	@ConversionAlias("childActionList")
	@ElementList
	private List<ReleaseRemoveRollbackAction> childActionList;

	@ConversionAlias("subActionRollbackList")
	@ElementList
	private List<ModelAction> subActionRollbackList;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseRemoveRollbackAction() {}

	public ReleaseRemoveRollbackAction(final UUID parentReleaseId, final UUID newReleaseId, final String description, final int index, final List<ReleaseRemoveRollbackAction> childActionList,
			final List<ModelAction> subActionRollbackList) {
		this.parentReleaseId = parentReleaseId;
		this.newReleaseId = newReleaseId;
		this.description = description;
		this.index = index;
		this.childActionList = childActionList;
		this.subActionRollbackList = subActionRollbackList;
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release parentRelease = ActionHelper.findRelease(parentReleaseId, context, this);

		final Release newRelease = new Release(description, newReleaseId);
		parentRelease.addChild(index, newRelease);
		executeSubActions(context, actionContext);
		executeChildActions(context, actionContext);

		return new ReleaseRemoveAction(newRelease.getId());
	}

	private void executeSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		for (final ModelAction subAction : subActionRollbackList)
			subAction.execute(context, actionContext);
	}

	private void executeChildActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		for (int i = childActionList.size() - 1; i >= 0; i--)
			childActionList.get(i).execute(context, actionContext);
	}

	@Override
	public UUID getReferenceId() {
		return parentReleaseId;
	}

	public UUID getNewReleaseId() {
		return newReleaseId;
	}
}
