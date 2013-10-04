package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.ScopeRemoveTagAssociationActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeRemoveTagAssociationActionEntity.class)
public class ScopeRemoveTagAssociationAction implements ScopeAction, TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID tagId;

	public ScopeRemoveTagAssociationAction() {}

	public ScopeRemoveTagAssociationAction(final UUID scopeId, final UUID tagId) {
		this.scopeId = scopeId;
		this.tagId = tagId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final Tag tag = ActionHelper.findTag(tagId, context, this);
		for (final TagAssociationMetadata metadata : context.<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType())) {
			if (!tag.equals(metadata.getTag())) continue;

			context.removeMetadata(metadata);
			return new ScopeAddTagAssociationAction(metadata);
		}

		throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_INEXISTENT);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	public UUID getScopeId() {
		return scopeId;
	}

	public void setScopeId(final UUID scopeId) {
		this.scopeId = scopeId;
	}

	public UUID getTagId() {
		return tagId;
	}

	public void setTagId(final UUID tagId) {
		this.tagId = tagId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}

}
