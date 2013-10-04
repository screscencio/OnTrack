package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseUpdatePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

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

	public ReleaseUpdatePriorityAction() {}

	public ReleaseUpdatePriorityAction(final UUID releaseId, final int targetIndex) {
		this.releaseId = releaseId;
		this.targetIndex = targetIndex;
	}

	@Override
	public ReleaseUpdatePriorityAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (targetIndex < 0) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ALREADY_THE_LEAST_PRIORITARY);

		final Release selectedRelease = ActionHelper.findRelease(releaseId, context, this);
		if (selectedRelease.isRoot()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CHANGE_ROOT_RELEASE_PRIORITY);

		final Release parentRelease = selectedRelease.getParent();
		if (targetIndex >= parentRelease.getChildren().size()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ALREADY_THE_MOST_PRIORITARY);

		final int currentIndex = parentRelease.getChildIndex(selectedRelease);
		parentRelease.removeChild(selectedRelease);
		parentRelease.addChild(targetIndex, selectedRelease);

		return new ReleaseUpdatePriorityAction(releaseId, currentIndex);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

	public UUID getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final UUID releaseId) {
		this.releaseId = releaseId;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(final int targetIndex) {
		this.targetIndex = targetIndex;
	}

}
