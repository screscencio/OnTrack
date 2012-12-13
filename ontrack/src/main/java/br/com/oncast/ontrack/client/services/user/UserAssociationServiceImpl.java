package br.com.oncast.ontrack.client.services.user;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.UserAssociationTag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

public class UserAssociationServiceImpl implements UserAssociationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;

	public UserAssociationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
	}

	@Override
	public List<UserRepresentation> getAssociatedUsers(final Scope scope) {
		final List<UserRepresentation> users = new ArrayList<UserRepresentation>();
		final List<UserAssociationTag> tags = contextProviderService.getCurrent().getTags(scope, UserAssociationTag.getType());
		for (final UserAssociationTag tag : tags) {
			users.add(tag.getUser());
		}
		return users;
	}

	@Override
	public boolean onAssociateUserRequest(final Scope scope, final UserRepresentation user) {
		if (hasAssociatedUser(scope, user)) return false;

		actionExecutionService.onUserActionExecutionRequest(new ScopeAddAssociatedUserAction(scope.getId(), user.getId()));
		return true;
	}

	@Override
	public void onUserRemoveAssociationRequest(final Scope scope, final UserRepresentation user) {
		if (!hasAssociatedUser(scope, user)) return;

		actionExecutionService.onUserActionExecutionRequest(new ScopeRemoveAssociatedUserAction(scope.getId(), user.getId()));
	}

	@Override
	public boolean hasAssociatedUser(final Scope scope, final UserRepresentation user) {
		return getAssociatedUsers(scope).contains(user);
	}

}
