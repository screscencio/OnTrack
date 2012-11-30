package br.com.oncast.ontrack.shared.model.action;

import java.util.List;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveAssociatedUserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.UserAssociationTag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeRemoveAssociatedUserActionEntity.class)
public class ScopeRemoveAssociatedUserAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID userId;

	protected ScopeRemoveAssociatedUserAction() {}

	public ScopeRemoveAssociatedUserAction(final UUID scopeId, final UUID userId) {
		this.scopeId = scopeId;
		this.userId = userId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context);
		final UserRepresentation user = ActionHelper.findUser(userId, context);

		final List<UserAssociationTag> tags = context.getTags(scope, UserAssociationTag.getType());

		for (final UserAssociationTag tag : tags) {
			if (tag.getUser().equals(user)) {
				context.removeTag(tag);
				return new ScopeAddAssociatedUserAction(tag);
			}
		}

		throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.UNKNOWN, "Tag not found");
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
