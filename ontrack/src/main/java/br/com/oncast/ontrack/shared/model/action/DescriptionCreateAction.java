package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(DescriptionCreateActionEntity.class)
public class DescriptionCreateAction implements DescriptionAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private String description;

	@Element(required = false)
	private UUID descriptionId;

	protected DescriptionCreateAction() {}

	public DescriptionCreateAction(final UUID subjectId, final String description) {
		this.subjectId = subjectId;
		this.description = description;
		this.descriptionId = new UUID();
	}

	public DescriptionCreateAction(final UUID subjectId, final Description description) {
		this.subjectId = subjectId;
		this.description = description.getDescription();
		this.descriptionId = description.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (description == null || description.isEmpty()) throw new UnableToCompleteActionException(
				ActionExecutionErrorMessageCode.DESCRIPTION_WITH_EMPTY_MESSAGE);

		context.addDescription(getDescription(context, actionContext), subjectId);

		return new DescriptionRemoveAction(subjectId, descriptionId, false);
	}

	private Description getDescription(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context);

		return new Description(descriptionId, author, actionContext.getTimestamp(), description);
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
