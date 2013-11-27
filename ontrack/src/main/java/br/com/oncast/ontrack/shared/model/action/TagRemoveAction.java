package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.IgnoreByConversion;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(TagRemoveActionEntity.class)
public class TagRemoveAction implements TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID tagId;

	@IgnoreByConversion
	@ElementList(required = false)
	private List<UUID> removedScopes;

	protected TagRemoveAction() {}

	public TagRemoveAction(final UUID tagId) {
		this.tagId = tagId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Tag tag = ActionHelper.findTag(tagId, context, this);

		final List<ModelAction> rollbackActions = removeAssociations(context, actionContext);
		context.removeTag(tag);

		return new TagCreateAction(tag, rollbackActions);
	}

	public List<UUID> getRemovedScopes() {
		return removedScopes == null ? removedScopes = new ArrayList<UUID>() : removedScopes;
	}

	private List<ModelAction> removeAssociations(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		removedScopes = new ArrayList<UUID>();

		for (final TagAssociationMetadata metadata : context.<TagAssociationMetadata> getAllMetadata(TagAssociationMetadata.getType())) {
			if (!metadata.getTag().equals(tagId)) continue;
			rollbackActions.add(new ScopeRemoveTagAssociationAction(metadata.getSubject().getId(), tagId).execute(context, actionContext));
			removedScopes.add(metadata.getSubject().getId());
		}
		return rollbackActions;
	}

	@Override
	public UUID getReferenceId() {
		return tagId;
	}

}
