package br.com.oncast.ontrack.shared.model.actions;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

@ConvertTo(ReleaseCreateActionEntity.class)
public class ReleaseCreateActionDefault implements ReleaseCreateAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element(required = false)
	private UUID parentReleaseId;

	@ConversionAlias("newReleaseId")
	@Element
	private UUID newReleaseId;

	@ConversionAlias("description")
	@Attribute
	private String description;

	@ConversionAlias("subAction")
	@Element(required = false)
	@IgnoredByDeepEquality
	private ReleaseCreateActionDefault subReleaseCreateAction;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseCreateActionDefault() {}

	// The new release id must be created in constructors. If it is not created here, the actions cannot be executed correctly in the server side.
	public ReleaseCreateActionDefault(final String releaseDescription) {
		this.description = releaseDescription;
		newReleaseId = new UUID();
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		Release parentRelease = context.getProjectRelease();

		Release newRelease = null;
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(description);

		do {
			try {
				parentRelease = parentRelease.findRelease(parser.getHeadRelease());
			}
			catch (final ReleaseNotFoundException e) {
				newRelease = new Release(parser.getHeadRelease(), newReleaseId);
				break;
			}
		} while (parser.next());

		parentRelease.addChild(newRelease);

		if (parser.next()) createSubRelease(context);

		return new ReleaseRemoveAction(newReleaseId);
	}

	private void createSubRelease(final ProjectContext context) throws UnableToCompleteActionException {
		if (subReleaseCreateAction == null) subReleaseCreateAction = new ReleaseCreateActionDefault(description);
		subReleaseCreateAction.execute(context);
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
