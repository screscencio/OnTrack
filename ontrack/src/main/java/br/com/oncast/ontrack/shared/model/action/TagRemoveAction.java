package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(TagRemoveActionEntity.class)
public class TagRemoveAction implements TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID tagId;

	protected TagRemoveAction() {}

	public TagRemoveAction(final UUID tagId) {
		this.tagId = tagId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Tag removedTag = context.removeTag(tagId);
		if (removedTag == null) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.REMOVE_INEXISTENT);
		return new TagCreateAction(removedTag);
	}

	@Override
	public UUID getReferenceId() {
		return tagId;
	}

}
