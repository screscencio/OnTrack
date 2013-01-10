package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TagCreateActionEntity.class)
public class TagCreateAction implements TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID tagId;

	@Element
	private String description;

	@Element
	private Color backgroundColor;

	@Element
	private Color textColor;

	protected TagCreateAction() {}

	public TagCreateAction(final String description, final Color backgroundColor, final Color textColor) {
		this.description = description.trim();
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.tagId = new UUID();
	}

	public TagCreateAction(final Tag tag) {
		this.description = tag.getDescription();
		this.backgroundColor = tag.getBackgroundColor();
		this.textColor = tag.getTextColor();
		this.tagId = tag.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (context.hasTag(description)) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CREATE_EXISTENT);
		context.addTag(new Tag(tagId, description, backgroundColor, textColor));
		return new TagRemoveAction(tagId);
	}

	@Override
	public UUID getReferenceId() {
		return tagId;
	}

}
