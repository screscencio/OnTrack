package br.com.oncast.ontrack.shared.model.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseRemoveAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;
	private final UUID referenceId;

	public ReleaseRemoveAction(final UUID selectedReleaseId) {
		this.referenceId = selectedReleaseId;
	}

	@Override
	public ReleaseRemoveRollbackAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release selectedRelease = ReleaseActionHelper.findRelease(referenceId, context);
		if (selectedRelease.isRoot()) throw new UnableToCompleteActionException("Unable to remove root level.");

		final List<ReleaseRemoveRollbackAction> childActionRollbackList = removeDescendantsReleases(context, selectedRelease);
		selectedRelease.removeAllScopes();

		final Release parentRelease = selectedRelease.getParent();
		final int index = parentRelease.getChildIndex(selectedRelease);
		parentRelease.removeChild(selectedRelease);

		return new ReleaseRemoveRollbackAction(parentRelease.getId(), referenceId, selectedRelease.getDescription(), index, childActionRollbackList);
	}

	private List<ReleaseRemoveRollbackAction> removeDescendantsReleases(final ProjectContext context, final Release selectedRelease)
			throws UnableToCompleteActionException {
		final List<ReleaseRemoveRollbackAction> childActionRollbackList = new ArrayList<ReleaseRemoveRollbackAction>();
		for (final Release child : new ArrayList<Release>(selectedRelease.getChildren()))
			childActionRollbackList.add(new ReleaseRemoveAction(child.getId()).execute(context));

		return childActionRollbackList;
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}
