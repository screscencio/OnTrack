package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
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

	@ConversionAlias("subReleaseCreateAction")
	private ReleaseCreateActionDefault subReleaseCreateAction;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseCreateActionDefault() {}

	// The new release id must be created in constructors. If it is not created here, the actions cannot be executed correctly in the server side.
	public ReleaseCreateActionDefault(final String releaseDescription) {
		this.releaseDescription = releaseDescription;
		newReleaseId = new UUID();
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		Release parentRelease = context.getProjectRelease();

		Release newRelease = null;
		final String[] releaseLevels = releaseDescription.split(Release.SEPARATOR);

		int i = 0;
		while (i < releaseLevels.length) {
			try {
				parentRelease = context.findRelease(getReleaseQuery(releaseLevels, i));
			}
			catch (final ReleaseNotFoundException e) {
				newRelease = new Release(releaseLevels[i], newReleaseId);
				break;
			}
			i++;
		}

		parentRelease.addChild(newRelease);

		if (releaseLevels.length > i + 1) createSubRelease(context);

		return new ReleaseRemoveAction(newReleaseId);
	}

	private void createSubRelease(final ProjectContext context) throws UnableToCompleteActionException {
		if (subReleaseCreateAction == null) subReleaseCreateAction = new ReleaseCreateActionDefault(releaseDescription);
		subReleaseCreateAction.execute(context);
	}

	private String getReleaseQuery(final String[] releaseLevels, final int index) {
		final StringBuilder query = new StringBuilder();
		query.append(releaseLevels[0]);

		for (int i = 1; i <= index; i++) {
			query.append(Release.SEPARATOR);
			query.append(releaseLevels[i]);
		}
		return query.toString();
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
