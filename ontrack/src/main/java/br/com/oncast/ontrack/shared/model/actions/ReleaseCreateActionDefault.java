package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseCreateActionEntity.class)
public class ReleaseCreateActionDefault implements ReleaseCreateAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	private UUID parentReleaseId;

	@ConversionAlias("newReleaseId")
	private UUID newReleaseId;

	@ConversionAlias("releaseDescription")
	private String releaseDescription;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseCreateActionDefault() {}

	// The new release id must be created in constructors. If it is not created here, the actions cannot be executed correctly in the server side.
	public ReleaseCreateActionDefault(final String releaseDescription) {
		this.releaseDescription = releaseDescription;
		newReleaseId = new UUID();
	}

	public ReleaseCreateActionDefault(final UUID parentReleaseId, final String releaseDescription) {
		this.parentReleaseId = parentReleaseId;
		this.releaseDescription = releaseDescription;
		newReleaseId = new UUID();
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		Release parentRelease;
		if (isParentReleaseSpecified()) parentRelease = ReleaseActionHelper.findRelease(parentReleaseId, context);
		else parentRelease = context.getProjectRelease();

		final String[] releaseLevels = releaseDescription.split(Release.SEPARATOR);
		final Release newRelease = new Release(releaseLevels[0], newReleaseId);
		parentRelease.addChild(newRelease);

		if (releaseLevels.length > 1) createSubRelease(newReleaseId, releaseLevels, context);

		return new ReleaseRemoveAction(newReleaseId);
	}

	private void createSubRelease(final UUID parentId, final String[] releaseLevels, final ProjectContext context)
			throws UnableToCompleteActionException {

		new ReleaseCreateActionDefault(parentId, releaseDescription.substring(releaseLevels[0].length() + Release.SEPARATOR.length())).execute(context);
	}

	private boolean isParentReleaseSpecified() {
		return parentReleaseId != null;
	}

	@Override
	public UUID getReferenceId() {
		return parentReleaseId;
	}

	@Override
	public UUID getNewReleaseId() {
		return newReleaseId;
	}

}
