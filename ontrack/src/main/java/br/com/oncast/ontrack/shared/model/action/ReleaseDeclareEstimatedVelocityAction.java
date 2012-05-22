package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseDeclareEstimatedVelocityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
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
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		if (estimatedVelocity != null && estimatedVelocity < 0.001f) throw new UnableToCompleteActionException("Cant declare estimated velocity as 0.");
		final Release release = ReleaseActionHelper.findRelease(releaseId, context);
		final Float previousDeclaration = release.hasDeclaredEstimatedVelocity() ? release.getEstimatedVelocity() : null;

		release.declareEstimatedVelocity(estimatedVelocity);
		return new ReleaseDeclareEstimatedVelocityAction(releaseId, previousDeclaration);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

}
