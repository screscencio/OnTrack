package br.com.oncast.ontrack.shared.model.actions;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRemoveRollbackActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseRemoveRollbackActionEntity.class)
public class ReleaseRemoveRollbackAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID newReleaseId;

	@ConversionAlias("parentReleaseId")
	private UUID parentReleaseId;

	@ConversionAlias("description")
	private String description;

	@ConversionAlias("index")
	private int index;

	@ConversionAlias("childActionList")
	private List<ReleaseRemoveRollbackAction> childActionList;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseRemoveRollbackAction() {}

	public ReleaseRemoveRollbackAction(final UUID parentReleaseId, final UUID newReleaseId, final String description, final int index,
			final List<ReleaseRemoveRollbackAction> childActionList) {
		this.parentReleaseId = parentReleaseId;
		this.newReleaseId = newReleaseId;
		this.description = description;
		this.index = index;
		this.childActionList = childActionList;
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release parentRelease = ReleaseActionHelper.findRelease(parentReleaseId, context);

		final Release newRelease = new Release(description, newReleaseId);
		parentRelease.addChild(index, newRelease);
		executeChildActions(context);

		return new ReleaseRemoveAction(newRelease.getId());
	}

	private void executeChildActions(final ProjectContext context) throws UnableToCompleteActionException {
		for (int i = childActionList.size() - 1; i >= 0; i--)
			childActionList.get(i).execute(context);
	}

	@Override
	public UUID getReferenceId() {
		return parentReleaseId;
	}
}
