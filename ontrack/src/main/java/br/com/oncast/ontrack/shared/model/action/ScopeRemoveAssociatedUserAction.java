package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveAssociatedUserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeRemoveAssociatedUserActionEntity.class)
public class ScopeRemoveAssociatedUserAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID userId;

	public ScopeRemoveAssociatedUserAction() {}

	public ScopeRemoveAssociatedUserAction(final UUID scopeId, final UUID userId) {
		this.scopeId = scopeId;
		this.userId = userId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final UserRepresentation user = ActionHelper.findUser(userId, context, this);

		final List<UserAssociationMetadata> metadataList = context.getMetadataList(scope, UserAssociationMetadata.getType());

		for (final UserAssociationMetadata metadata : metadataList) {
			if (metadata.getUser().equals(user)) {
				context.removeMetadata(metadata);
				return new ScopeAddAssociatedUserAction(metadata);
			}
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

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID userId) {
		this.userId = userId;
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
