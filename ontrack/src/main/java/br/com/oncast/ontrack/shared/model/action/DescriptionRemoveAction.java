package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(DescriptionRemoveActionEntity.class)
public class DescriptionRemoveAction implements DescriptionAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID descriptionId;

	@Element
	private boolean userAction;

	protected DescriptionRemoveAction() {}

	public DescriptionRemoveAction(final UUID subjectId, final UUID descriptionId, final boolean isUserAction) {
		this.subjectId = subjectId;
		this.descriptionId = descriptionId;
		this.userAction = isUserAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Description description = ActionHelper.findDescription(subjectId, context);
		if (userAction && !description.getAuthor().getId().equals(actionContext.getUserId())) throw new UnableToCompleteActionException(
				ActionExecutionErrorMessageCode.DESCRIPTION_REMOVE);

		context.removeDescriptionFor(subjectId);

		return new DescriptionCreateAction(subjectId, description);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	@Override
	public UUID getDescriptionId() {
		return descriptionId;
	}

}
