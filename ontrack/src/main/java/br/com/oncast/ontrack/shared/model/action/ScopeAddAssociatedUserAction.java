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

	protected ScopeAddAssociatedUserAction() {}

	public ScopeAddAssociatedUserAction(final UUID scopeId, final UUID userId) {
		this(scopeId, userId, new UUID());
	}

	protected ScopeAddAssociatedUserAction(final UserAssociationMetadata metadata) {
		this(metadata.getSubject().getId(), metadata.getUser().getId(), metadata.getId());
	}

	private ScopeAddAssociatedUserAction(final UUID scopeId, final UUID userId, final UUID metadataId) {
		this.scopeId = scopeId;
		this.userId = userId;
		this.metadataId = metadataId;
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
