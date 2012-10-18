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
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestCallback;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequestResponse;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOfflineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOnlineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

public class UsersStatusServiceImpl implements UsersStatusService {

	private final DispatchService requestDispatchService;
	private final ContextProviderService contextProviderService;
	private SortedSet<User> activeUsers;
	private SortedSet<User> onlineUsers;
	private final Set<UsersStatusChangeListener> listenersList;

	public UsersStatusServiceImpl(final DispatchService requestDispatchService, final ContextProviderService contextProviderService,
			final ServerPushClientService serverPushClientService, final EventBus eventBus) {
		this.requestDispatchService = requestDispatchService;
		this.contextProviderService = contextProviderService;

		listenersList = new HashSet<UsersStatusChangeListener>();

		contextProviderService.addContextLoadListener(getContextChangeListener());

		registerServerPushEventHandlers(contextProviderService, serverPushClientService);
	}

	private void registerServerPushEventHandlers(final ContextProviderService contextProviderService, final ServerPushClientService serverPushClientService) {
		serverPushClientService.registerServerEventHandler(UserOpenProjectEvent.class, new ServerPushEventHandler<UserOpenProjectEvent>() {
			@Override
			public void onEvent(final UserOpenProjectEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserId());
					activeUsers.add(user);
					notifyUsersStatusListsUpdate();
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
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserId());
					activeUsers.remove(user);
					notifyUsersStatusListsUpdate();
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		});

		serverPushClientService.registerServerEventHandler(UserOnlineEvent.class, new ServerPushEventHandler<UserOnlineEvent>() {
			@Override
			public void onEvent(final UserOnlineEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserId());
					onlineUsers.add(user);
					notifyUsersStatusListsUpdate();
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		});

		serverPushClientService.registerServerEventHandler(UserOfflineEvent.class, new ServerPushEventHandler<UserOfflineEvent>() {
			@Override
			public void onEvent(final UserOfflineEvent event) {
				try {
					final User user = contextProviderService.getCurrentProjectContext().findUser(event.getUserId());
					onlineUsers.remove(user);
					notifyUsersStatusListsUpdate();
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
					clearUsersStatus();
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
		clearUsersStatus();

		requestDispatchService.dispatch(new UsersStatusRequest(projectId), new ActiveUsersRequestCallback() {

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				notifyError(caught);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				notifyError(caught);
			}

			@Override
			public void onSuccess(final UsersStatusRequestResponse result) {
				activeUsers = retrieveUsers(result.getActiveUsers());
				onlineUsers = retrieveUsers(result.getOnlineUsers());

				notifyUsersStatusListsUpdate();
			}

			private SortedSet<User> retrieveUsers(final Set<UUID> usersIds) {
				final SortedSet<User> users = new TreeSet<User>();

				final ProjectContext context = contextProviderService.getCurrentProjectContext();

				for (final UUID userId : usersIds) {
					try {
						users.add(context.findUser(userId));
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
		if (hasLoadedActiveUsers()) usersStatusChangeListener.onUsersStatusListsUpdated(activeUsers, onlineUsers);

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

	@Override
	public SortedSet<User> getOnlineUsers() {
		if (!hasLoadedOnlineUsers()) throw new RuntimeException("There is no loaded active users");
		return activeUsers;
	}

	private boolean hasLoadedOnlineUsers() {
		return onlineUsers != null;
	}

	private void clearUsersStatus() {
		activeUsers = null;
		onlineUsers = null;
	}

	public interface UsersStatusChangeListener {

		void onUsersStatusListUnavailable(Throwable caught);

		void onUsersStatusListsUpdated(SortedSet<User> activeUsers, SortedSet<User> onlineUsers);
	}

	private void notifyUsersStatusListsUpdate() {
		for (final UsersStatusChangeListener callback : new HashSet<UsersStatusChangeListener>(listenersList)) {
			callback.onUsersStatusListsUpdated(activeUsers, onlineUsers);
		}
	}

	private void notifyError(final Throwable caught) {
		for (final UsersStatusChangeListener callback : new HashSet<UsersStatusChangeListener>(listenersList)) {
			callback.onUsersStatusListUnavailable(caught);
		}
	}

}
