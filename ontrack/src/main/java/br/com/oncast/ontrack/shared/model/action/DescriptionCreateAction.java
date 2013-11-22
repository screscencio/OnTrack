package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(DescriptionCreateActionEntity.class)
public class DescriptionCreateAction implements DescriptionAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private String description;

	@Element(required = false)
	private UUID descriptionId;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	protected DescriptionCreateAction() {}

	public DescriptionCreateAction(final UUID subjectId, final String description) {
		this.uniqueId = new UUID();
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
		if (description == null || description.isEmpty()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.DESCRIPTION_WITH_EMPTY_MESSAGE);

		context.addDescription(getDescription(context, actionContext), subjectId);

		return new DescriptionRemoveAction(subjectId, descriptionId, false);
	}

	private Description getDescription(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context, this);

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

	@Override
	public String getDescription() {
		return description;
	}
}
