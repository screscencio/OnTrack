package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.description.DescriptionRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(DescriptionRemoveActionEntity.class)
public class DescriptionRemoveAction implements DescriptionAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID descriptionId;

	@Attribute
	private Boolean userAction;

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

	protected DescriptionRemoveAction() {}

	public DescriptionRemoveAction(final UUID subjectId, final UUID descriptionId, final boolean isUserAction) {
		this.uniqueId = new UUID();
		this.subjectId = subjectId;
		this.descriptionId = descriptionId;
		this.userAction = isUserAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Description description = ActionHelper.findDescription(subjectId, context, this);
		if (userAction && !description.getAuthor().getId().equals(actionContext.getUserId())) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.DESCRIPTION_REMOVE);

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

	@Override
	public String getDescription() {
		return "";
	}
}
