package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

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

@ConvertTo(ScopeRemoveTagAssociationActionEntity.class)
public class ScopeRemoveTagAssociationAction implements ScopeAction, TagAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID tagId;

	protected ScopeRemoveTagAssociationAction() {}

	public ScopeRemoveTagAssociationAction(final UUID scopeId, final UUID tagId) {
		this.scopeId = scopeId;
		this.tagId = tagId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context);
		final Tag tag = ActionHelper.findTag(tagId, context);
		for (final TagAssociationMetadata metadata : context.<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType())) {
			if (!tag.equals(metadata.getTag())) continue;

			context.removeMetadata(metadata);
			return new ScopeAddTagAssociationAction(scopeId, tagId);
		}

		throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.REMOVE_INEXISTENT);
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
