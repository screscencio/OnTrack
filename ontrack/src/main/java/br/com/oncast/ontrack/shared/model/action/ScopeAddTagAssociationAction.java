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

	protected ScopeAddTagAssociationAction() {}

	public ScopeAddTagAssociationAction(final UUID scopeId, final UUID tagId) {
		this(scopeId, tagId, new UUID());
	}

	protected ScopeAddTagAssociationAction(final TagAssociationMetadata metadata) {
		this(metadata.getSubject().getId(), metadata.getTag().getId(), metadata.getId());
	}

	private ScopeAddTagAssociationAction(final UUID scopeId, final UUID tagId, final UUID metadataId) {
		this.scopeId = scopeId;
		this.tagId = tagId;
		this.metadataId = metadataId;
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

}
