package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEstimatedVelocityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseDeclareEstimatedVelocityActionEntity.class)
public class ReleaseDeclareEstimatedVelocityAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID releaseId;

	@Attribute(required = false)
	private Float estimatedVelocity;

	protected ReleaseDeclareEstimatedVelocityAction() {}

	public ReleaseDeclareEstimatedVelocityAction(final UUID releaseId, final Float decalredEstimatedVelocity) {
		this.releaseId = releaseId;
		this.estimatedVelocity = decalredEstimatedVelocity;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (estimatedVelocity != null && estimatedVelocity < 0.01F) throw new UnableToCompleteActionException(
				this, ActionExecutionErrorMessageCode.DECLARE_ESTIMATED_VELOCITY_AS_ZERO);
		final Release release = ActionHelper.findRelease(releaseId, context, this);
		final Float previousDeclaration = release.hasDeclaredEstimatedSpeed() ? release.getEstimatedSpeed() : null;

		release.declareEstimatedVelocity(estimatedVelocity);
		return new ReleaseDeclareEstimatedVelocityAction(releaseId, previousDeclaration);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

}
