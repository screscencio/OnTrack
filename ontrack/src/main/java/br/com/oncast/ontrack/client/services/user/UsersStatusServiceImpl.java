package br.com.oncast.ontrack.client.services.user;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestCallback;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class UsersStatusServiceImpl implements UsersStatusService {

	private final DispatchService requestDispatchService;
	private final ContextProviderService contextProviderService;

	public UsersStatusServiceImpl(final DispatchService requestDispatchService, final ContextProviderService contextProviderService) {
		this.requestDispatchService = requestDispatchService;
		this.contextProviderService = contextProviderService;

	}

	@Override
	public void getActiveUsers(final AsyncCallback<Set<User>> callback) {
		final ProjectContext currentProjectContext = contextProviderService.getCurrentProjectContext();
		final UUID projectId = currentProjectContext.getProjectRepresentation().getId();
		requestDispatchService.dispatch(new ActiveUsersRequest(projectId), new ActiveUsersRequestCallback() {

			@Override
			public void onSuccess(final ActiveUsersRequestResponse result) {
				final HashSet<User> users = new HashSet<User>();
				for (final String element : result.getActiveUsers()) {
					try {
						users.add(currentProjectContext.findUser(element));
					}
					catch (final UserNotFoundException e) {}
				}
				callback.onSuccess(users);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
}
