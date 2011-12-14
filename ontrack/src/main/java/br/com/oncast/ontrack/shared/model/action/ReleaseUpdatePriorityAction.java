package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseUpdatePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO Change this action, so it receives the id of the target release, allowing this action to move a child release to another one.
@ConvertTo(ReleaseUpdatePriorityActionEntity.class)
public class ReleaseUpdatePriorityAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID releaseId;

	@ConversionAlias("targetIndex")
	@Attribute
	private int targetIndex;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseUpdatePriorityAction() {}

	public ReleaseUpdatePriorityAction(final UUID releaseId, final int targetIndex) {
		this.releaseId = releaseId;
		this.targetIndex = targetIndex;
	}

	@Override
	public ReleaseUpdatePriorityAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		if (targetIndex < 0) throw new UnableToCompleteActionException("Unable to change release priority to a negative position.");

		final Release selectedRelease = ReleaseActionHelper.findRelease(releaseId, context);
		if (selectedRelease.isRoot()) throw new UnableToCompleteActionException("Unable to change priority of a root level.");

		final Release parentRelease = selectedRelease.getParent();
		if (targetIndex >= parentRelease.getChildren().size()) throw new UnableToCompleteActionException(
				"The release priority could not be updated because the target position is not within the allowed range. You are trying to update for position "
						+ targetIndex + ", but there are only " + parentRelease.getChildren().size() + " itens in the parent release.");

		final int currentIndex = parentRelease.getChildIndex(selectedRelease);
		parentRelease.removeChild(selectedRelease);
		parentRelease.addChild(targetIndex, selectedRelease);

		return new ReleaseUpdatePriorityAction(releaseId, currentIndex);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

}
