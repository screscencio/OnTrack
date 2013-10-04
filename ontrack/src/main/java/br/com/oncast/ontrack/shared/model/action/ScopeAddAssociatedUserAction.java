package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeAddAssociatedUserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeAddAssociatedUserActionEntity.class)
public class ScopeAddAssociatedUserAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID userId;

	@Element
	private UUID metadataId;

	public ScopeAddAssociatedUserAction() {}

	public ScopeAddAssociatedUserAction(final UUID scopeId, final UUID userId) {
		this.scopeId = scopeId;
		this.userId = userId;
		this.metadataId = new UUID();
	}

	protected ScopeAddAssociatedUserAction(final UserAssociationMetadata metadata) {
		this.scopeId = metadata.getSubject().getId();
		this.userId = metadata.getUser().getId();
		this.metadataId = metadata.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final UserRepresentation user = ActionHelper.findUser(userId, context, this);

		for (final UserAssociationMetadata metadata : context.<UserAssociationMetadata> getMetadataList(scope, UserAssociationMetadata.getType())) {
			if (user.equals(metadata.getUser())) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CREATE_EXISTENT);
		}

		context.addMetadata(MetadataFactory.createUserMetadata(metadataId, scope, user));

		return new ScopeRemoveAssociatedUserAction(scopeId, userId);
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

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID userId) {
		this.userId = userId;
	}

	public UUID getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(final UUID metadataId) {
		this.metadataId = metadataId;
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
