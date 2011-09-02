package br.com.oncast.ontrack.shared.model.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseRemoveActionEntity.class)
public class ReleaseRemoveAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID referenceId;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseRemoveAction() {}

	public ReleaseRemoveAction(final UUID selectedReleaseId) {
		this.referenceId = selectedReleaseId;
	}

	@Override
	public ReleaseRemoveRollbackAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Release selectedRelease = ReleaseActionHelper.findRelease(referenceId, context);
		if (selectedRelease.isRoot()) throw new UnableToCompleteActionException("Unable to remove root level.");

		final List<ReleaseRemoveRollbackAction> childActionRollbackList = removeDescendantsReleases(context, selectedRelease);
		final List<ScopeBindReleaseAction> subActionRollbackList = dissociateScopesFromThisRelease(context, selectedRelease);

		final Release parentRelease = selectedRelease.getParent();
		final int index = parentRelease.getChildIndex(selectedRelease);
		parentRelease.removeChild(selectedRelease);

		return new ReleaseRemoveRollbackAction(parentRelease.getId(), referenceId, selectedRelease.getDescription(), index, childActionRollbackList,
				subActionRollbackList);
	}

	private List<ScopeBindReleaseAction> dissociateScopesFromThisRelease(final ProjectContext context, final Release release)
			throws UnableToCompleteActionException {

		final List<ScopeBindReleaseAction> subActionRollbackList = new ArrayList<ScopeBindReleaseAction>();
		for (final Scope scope : release.getScopeList())
			subActionRollbackList.add(new ScopeBindReleaseAction(scope.getId(), null).execute(context));

		Collections.reverse(subActionRollbackList);
		return subActionRollbackList;
	}

	private List<ReleaseRemoveRollbackAction> removeDescendantsReleases(final ProjectContext context, final Release release)
			throws UnableToCompleteActionException {

		final List<ReleaseRemoveRollbackAction> childActionRollbackList = new ArrayList<ReleaseRemoveRollbackAction>();
		for (final Release child : release.getChildren())
			childActionRollbackList.add(new ReleaseRemoveAction(child.getId()).execute(context));

		return childActionRollbackList;
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}
