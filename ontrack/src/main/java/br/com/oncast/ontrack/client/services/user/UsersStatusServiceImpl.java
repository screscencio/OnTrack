package br.com.oncast.ontrack.client.services.user;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestCallback;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestResponse;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;

public class UsersStatusServiceImpl implements UsersStatusService {

	private final DispatchService requestDispatchService;
	private final ContextProviderService contextProviderService;
	private SortedSet<User> activeUsers;
	private final Set<UsersStatusChangeListener> listenersList;

	public UsersStatusServiceImpl(final DispatchService requestDispatchService, final ContextProviderService contextProviderService,
			final ServerPushClientService serverPushClientService) {
		this.requestDispatchService = requestDispatchService;
		this.contextProviderService = contextProviderService;
		listenersList = new HashSet<UsersStatusChangeListener>();

		contextProviderService.addContextLoadListener(getContextChangeListener());

		serverPushClientService.registerServerEventHandler(UserOpenProjectEvent.class, new ServerPushEventHandler<UserOpenProjectEvent>() {
			@Override
			public void onEvent(final UserOpenProjectEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserEmail());
					activeUsers.add(user);
					notifyUsersListUpdate();
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserOpenProjectEventHandler Failed", e);
				}
			}
		});

		serverPushClientService.registerServerEventHandler(UserClosedProjectEvent.class, new ServerPushEventHandler<UserClosedProjectEvent>() {
			@Override
			public void onEvent(final UserClosedProjectEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserEmail());
					activeUsers.remove(user);
					notifyUsersListUpdate();
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		});

		serverPushClientService.registerServerEventHandler(UserClosedProjectEvent.class, new ServerPushEventHandler<UserClosedProjectEvent>() {
			@Override
			public void onEvent(final UserClosedProjectEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserEmail());
					activeUsers.remove(user);
					notifyUsersListUpdate();
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		});
	}

	private ContextChangeListener getContextChangeListener() {
		return new ContextChangeListener() {
			@Override
			public void onProjectChanged(final UUID projectId) {
				if (projectId == null) {
					activeUsers = null;
					return;
				}

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						loadActiveUsers(projectId);
					}
				});
			}
		};
	}

	private void loadActiveUsers(final UUID projectId) {
		activeUsers = null;

		requestDispatchService.dispatch(new ActiveUsersRequest(projectId), new ActiveUsersRequestCallback() {

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				notifyError(caught);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				notifyError(caught);
			}

			@Override
			public void onSuccess(final ActiveUsersRequestResponse result) {
				activeUsers = retrieveUsers(result.getActiveUsers());

				notifyUsersListUpdate();
			}

			private SortedSet<User> retrieveUsers(final Set<String> usersEmails) {
				final SortedSet<User> users = new TreeSet<User>();

				final ProjectContext context = contextProviderService.getCurrentProjectContext();

				for (final String userEmail : usersEmails) {
					try {
						users.add(context.findUser(userEmail));
					}
					catch (final UserNotFoundException e) {
						GWT.log("LoadActiveUsers failed", e);
					}
				}
				return users;
			}

		});
	}

	@Override
	public HandlerRegistration register(final UsersStatusChangeListener usersStatusChangeListener) {
		listenersList.add(usersStatusChangeListener);
		if (hasLoadedActiveUsers()) usersStatusChangeListener.onActiveUsersListLoaded(activeUsers);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				listenersList.remove(usersStatusChangeListener);
			}
		};
	}

	@Override
	public SortedSet<User> getActiveUsers() {
		if (!hasLoadedActiveUsers()) throw new RuntimeException("There is no loaded active users");
		return activeUsers;
	}

	private boolean hasLoadedActiveUsers() {
		return activeUsers != null;
	}

	public interface UsersStatusChangeListener {

		void onActiveUsersListUnavailable(Throwable caught);

		void onActiveUsersListLoaded(Set<User> activeUsers);
	}

	private void notifyUsersListUpdate() {
		for (final UsersStatusChangeListener callback : new HashSet<UsersStatusChangeListener>(listenersList)) {
			callback.onActiveUsersListLoaded(activeUsers);
		}
	}

	private void notifyError(final Throwable caught) {
		for (final UsersStatusChangeListener callback : new HashSet<UsersStatusChangeListener>(listenersList)) {
			callback.onActiveUsersListUnavailable(caught);
		}
	}

}
