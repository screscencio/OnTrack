package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagUpdateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.color.ColorPack;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TagUpdateActionEntity.class)
public class TagUpdateAction implements TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID tagId;

	@Element(required = false)
	private String description;

	@Element(required = false)
	private Color backgroundColor;

	@Element(required = false)
	private Color textColor;

	protected TagUpdateAction() {}

	public TagUpdateAction(final UUID tagId, final String description, final Color foregroundColor, final Color backgroundColor) {
		this.tagId = tagId;
		this.description = description == null ? "" : description.trim();
		this.backgroundColor = backgroundColor;
		this.textColor = foregroundColor;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (description.isEmpty() && backgroundColor == null && textColor == null) throw new UnableToCompleteActionException(
				ActionExecutionErrorMessageCode.UPDATE_WIHTOUT_CHANGES);
		final Tag tag = ActionHelper.findTag(tagId, context);
		final String previousDescription = tag.getDescription();
		final ColorPack previousColorPack = tag.getColorPack();

		tag.setDescription(description.isEmpty() ? previousDescription : description);
		tag.setColorPack(new ColorPack(textColor == null ? previousColorPack.getForeground() : textColor, backgroundColor == null ? previousColorPack
				.getBackground() : backgroundColor));

		return new TagUpdateAction(tagId, previousDescription, previousColorPack.getForeground(), previousColorPack.getBackground());
	}

	@Override
	public UUID getReferenceId() {
		return tagId;
	}

}
