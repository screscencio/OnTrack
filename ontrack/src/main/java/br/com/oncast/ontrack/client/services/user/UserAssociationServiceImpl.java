package br.com.oncast.ontrack.client.services.user;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.UserTag;
import br.com.oncast.ontrack.shared.model.user.User;

public class UserAssociationServiceImpl implements UserAssociationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;

	public UserAssociationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
	}

	@Override
	public List<User> getAssociatedUsers(final Scope scope) {
		final List<User> users = new ArrayList<User>();
		final List<UserTag> tags = contextProviderService.getCurrentProjectContext().getTags(scope, UserTag.getType());
		for (final UserTag tag : tags) {
			users.add(tag.getUser());
		}
		return users;
	}

	@Override
	public boolean onAssociateUserRequest(final Scope scope, final User user) {
		if (hasAssociatedUser(scope, user)) return false;

		actionExecutionService.onUserActionExecutionRequest(new ScopeAddAssociatedUserAction(scope.getId(), user.getId()));
		return true;
	}

	@Override
	public void onUserRemoveAssociationRequest(final Scope scope, final User user) {
		final List<UserTag> tags = contextProviderService.getCurrentProjectContext().getTags(scope, UserTag.getType());
		for (final UserTag tag : tags) {
			if (tag.getUser().equals(user)) {
				actionExecutionService.onUserActionExecutionRequest(new ScopeRemoveAssociatedUserAction(scope.getId(), tag.getId()));
				return;
			}
		}
	}

	@Override
	public boolean hasAssociatedUser(final Scope scope, final User user) {
		return getAssociatedUsers(scope).contains(user);
	}

}
