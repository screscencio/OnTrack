package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ReleaseCreateActionEntity.class)
public class ReleaseCreateAction implements ReleaseAction {

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
	private ReleaseCreateAction subReleaseCreateAction;

	public ReleaseCreateAction() {}

	// IMPORTANT The new release id must be created in constructors. If it is not created here, the actions cannot be executed correctly in the server side.
	public ReleaseCreateAction(final String releaseDescription) {
		this.description = releaseDescription;
		newReleaseId = new UUID();
	}

	@Override
	public ReleaseRemoveAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		Release parentRelease = context.getProjectRelease();

		Release newRelease = null;
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(description);

		do {
			try {
				parentRelease = parentRelease.findRelease(parser.getHeadRelease());
			} catch (final ReleaseNotFoundException e) {
				newRelease = new Release(parser.getHeadRelease(), newReleaseId);
				break;
			}
		} while (parser.next());

		parentRelease.addChild(newRelease);

		if (parser.next()) createSubRelease(context, actionContext);

		return new ReleaseRemoveAction(newReleaseId);
	}

	private void createSubRelease(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (subReleaseCreateAction == null) subReleaseCreateAction = new ReleaseCreateAction(description);
		subReleaseCreateAction.execute(context, actionContext);
	}

	@Override
	public UUID getReferenceId() {
		return parentReleaseId;
	}

	public UUID getNewReleaseId() {
		return newReleaseId;
	}

	public UUID getParentReleaseId() {
		return parentReleaseId;
	}

	public void setParentReleaseId(final UUID parentReleaseId) {
		this.parentReleaseId = parentReleaseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ReleaseCreateAction getSubReleaseCreateAction() {
		return subReleaseCreateAction;
	}

	public void setSubReleaseCreateAction(final ReleaseCreateAction subReleaseCreateAction) {
		this.subReleaseCreateAction = subReleaseCreateAction;
	}

	public void setNewReleaseId(final UUID newReleaseId) {
		this.newReleaseId = newReleaseId;
	}

}
