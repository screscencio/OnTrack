package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRenameActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ReleaseRenameActionEntity.class)
public class ReleaseRenameAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("newReleaseDescription")
	@Element
	private String newReleaseDescription;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseRenameAction() {}

	public ReleaseRenameAction(final UUID releaseId, final String newReleaseDescription) {
		this.referenceId = releaseId;
		this.newReleaseDescription = newReleaseDescription;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Release release = ActionHelper.findRelease(referenceId, context, this);
		final String oldDescription = release.getDescription();
		try {
			release.setDescription(newReleaseDescription);
		}
		catch (final Exception e) {
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.INVALID_RELEASE_DESCRIPTION);
		}
		return new ReleaseRenameAction(referenceId, oldDescription);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}
}
