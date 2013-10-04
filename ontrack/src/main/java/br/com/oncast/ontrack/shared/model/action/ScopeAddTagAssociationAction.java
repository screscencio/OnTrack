package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.ScopeAddTagAssociationActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeAddTagAssociationActionEntity.class)
public class ScopeAddTagAssociationAction implements ScopeAction, TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID metadataId;

	@Element
	private UUID scopeId;

	@Element
	private UUID tagId;

	public ScopeAddTagAssociationAction() {}

	public ScopeAddTagAssociationAction(final UUID scopeId, final UUID tagId) {
		this.scopeId = scopeId;
		this.tagId = tagId;
		this.metadataId = new UUID();
	}

	protected ScopeAddTagAssociationAction(final TagAssociationMetadata metadata) {
		this.scopeId = metadata.getSubject().getId();
		this.tagId = metadata.getTag().getId();
		this.metadataId = metadata.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final Tag tag = ActionHelper.findTag(tagId, context, this);

		for (final TagAssociationMetadata metadata : context.<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType())) {
			if (tag.equals(metadata.getTag())) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CREATE_EXISTENT);
		}

		context.addMetadata(MetadataFactory.createTagMetadata(metadataId, scope, tag));
		return new ScopeRemoveTagAssociationAction(scopeId, tagId);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
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

	public UUID getMetadataId() {
		return metadataId;
	}

	public UUID getScopeId() {
		return scopeId;
	}

	public UUID getTagId() {
		return tagId;
	}

	public void setMetadataId(final UUID metadataId) {
		this.metadataId = metadataId;
	}

	public void setScopeId(final UUID scopeId) {
		this.scopeId = scopeId;
	}

	public void setTagId(final UUID tagId) {
		this.tagId = tagId;
	}

}
