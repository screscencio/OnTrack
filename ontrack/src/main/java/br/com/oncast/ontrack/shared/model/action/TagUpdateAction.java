package br.com.oncast.ontrack.shared.model.action;

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

import org.simpleframework.xml.Element;

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

	public TagUpdateAction() {}

	public TagUpdateAction(final UUID tagId, final String description, final Color foregroundColor, final Color backgroundColor) {
		this.tagId = tagId;
		this.description = description == null ? "" : description.trim();
		this.backgroundColor = backgroundColor;
		this.textColor = foregroundColor;
	}

	public TagUpdateAction(final UUID tagId, final String description, final ColorPack colorPack) {
		this.tagId = tagId;
		this.description = description == null ? "" : description.trim();
		this.textColor = colorPack == null ? null : colorPack.getForeground();
		this.backgroundColor = colorPack == null ? null : colorPack.getBackground();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if (description.isEmpty() && backgroundColor == null && textColor == null) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.UPDATE_WIHTOUT_CHANGES);
		final Tag tag = ActionHelper.findTag(tagId, context, this);
		final String previousDescription = tag.getDescription();
		final ColorPack previousColorPack = tag.getColorPack();

		tag.setDescription(description.isEmpty() ? previousDescription : description);
		tag.setColorPack(new ColorPack(textColor == null ? previousColorPack.getForeground() : textColor, backgroundColor == null ? previousColorPack.getBackground() : backgroundColor));

		return new TagUpdateAction(tagId, previousDescription, previousColorPack.getForeground(), previousColorPack.getBackground());
	}

	@Override
	public UUID getReferenceId() {
		return tagId;
	}

	public UUID getTagId() {
		return tagId;
	}

	public void setTagId(final UUID tagId) {
		this.tagId = tagId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(final Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(final Color textColor) {
		this.textColor = textColor;
	}

}
