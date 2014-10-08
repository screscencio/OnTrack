package br.com.oncast.ontrack.client.services.user;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.context.ContextChangeListener;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequestResponse;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOfflineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOnlineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

public class UsersStatusServiceImpl implements UsersStatusService {

	private final DispatchService requestDispatchService;
	private final ContextProviderService contextProviderService;
	private SortedSet<UserRepresentation> activeUsers;
	private SortedSet<UserRepresentation> onlineUsers;
	private final Set<UsersStatusChangeListener> listenersList;
	private final SetMultimap<UserRepresentation, UserSpecificStatusChangeListener> userSpecificListeners;
	private final HashSet<HandlerRegistration> handlerRegistrations;
	private final ServerPushClientService serverPushClientService;

	public UsersStatusServiceImpl(final DispatchService requestDispatchService, final ContextProviderService contextProviderService, final ServerPushClientService serverPushClientService,
			final EventBus eventBus) {
		this.requestDispatchService = requestDispatchService;
		this.contextProviderService = contextProviderService;
		this.serverPushClientService = serverPushClientService;
		this.handlerRegistrations = new HashSet<HandlerRegistration>();

		listenersList = new HashSet<UsersStatusChangeListener>();
		userSpecificListeners = HashMultimap.create();

		contextProviderService.addContextLoadListener(getContextChangeListener());
	}

	private void registerServerPushEventHandlers(final ContextProviderService contextProviderService, final ServerPushClientService serverPushClientService) {
		handlerRegistrations.add(serverPushClientService.registerServerEventHandler(UserOpenProjectEvent.class, new ServerPushEventHandler<UserOpenProjectEvent>() {
			@Override
			public void onEvent(final UserOpenProjectEvent event) {
				try {
					final UserRepresentation user = contextProviderService.getCurrent().findUser(event.getUserId());
					activeUsers.add(user);
					notifyUsersStatusListsUpdate();
					notifyUserSpecificStatusChangeListeners(user, UserStatus.ACTIVE);
				} catch (final UserNotFoundException e) {
					GWT.log("UserOpenProjectEventHandler Failed", e);
				}
			}
		}));

		handlerRegistrations.add(serverPushClientService.registerServerEventHandler(UserClosedProjectEvent.class, new ServerPushEventHandler<UserClosedProjectEvent>() {
			@Override
			public void onEvent(final UserClosedProjectEvent event) {
				try {
					final UserRepresentation user = contextProviderService.getCurrent().findUser(event.getUserId());
					activeUsers.remove(user);
					notifyUsersStatusListsUpdate();
					notifyUserSpecificStatusChangeListeners(user, UserStatus.ONLINE);
				} catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		}));

		handlerRegistrations.add(serverPushClientService.registerServerEventHandler(UserOnlineEvent.class, new ServerPushEventHandler<UserOnlineEvent>() {
			@Override
			public void onEvent(final UserOnlineEvent event) {
				try {
					final UserRepresentation user = contextProviderService.getCurrent().findUser(event.getUserId());
					onlineUsers.add(user);
					notifyUsersStatusListsUpdate();
					notifyUserSpecificStatusChangeListeners(user, UserStatus.ONLINE);
				} catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		}));

		handlerRegistrations.add(serverPushClientService.registerServerEventHandler(UserOfflineEvent.class, new ServerPushEventHandler<UserOfflineEvent>() {
			@Override
			public void onEvent(final UserOfflineEvent event) {
				try {
					final UserRepresentation user = contextProviderService.getCurrent().findUser(event.getUserId());
					onlineUsers.remove(user);
					notifyUsersStatusListsUpdate();
					notifyUserSpecificStatusChangeListeners(user, UserStatus.OFFLINE);
				} catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		}));

	}

	private ContextChangeListener getContextChangeListener() {
		return new ContextChangeListener() {
			@Override
			public void onProjectChanged(final UUID projectId, final Long loadedProjectRevision) {
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

		requestDispatchService.dispatch(new UsersStatusRequest(projectId), new DispatchCallback<UsersStatusRequestResponse>() {

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

				registerServerPushEventHandlers(contextProviderService, serverPushClientService);

				notifyUsersStatusListsUpdate();

				for (final UserRepresentation user : onlineUsers) {
					notifyUserSpecificStatusChangeListeners(user, UserStatus.ONLINE);
				}
				for (final UserRepresentation user : activeUsers) {
					notifyUserSpecificStatusChangeListeners(user, UserStatus.ACTIVE);
				}
			}

			private SortedSet<UserRepresentation> retrieveUsers(final Set<UUID> usersIds) {
				final SortedSet<UserRepresentation> users = new TreeSet<UserRepresentation>();

				final ProjectContext context = contextProviderService.getCurrent();

				for (final UUID userId : usersIds) {
					try {
						users.add(context.findUser(userId));
					} catch (final UserNotFoundException e) {
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
	public SortedSet<UserRepresentation> getActiveUsers() {
		if (!hasLoadedActiveUsers()) throw new RuntimeException("There is no loaded active users");
		return activeUsers;
	}

	private boolean hasLoadedActiveUsers() {
		return activeUsers != null;
	}

	@Override
	public SortedSet<UserRepresentation> getOnlineUsers() {
		if (!hasLoadedOnlineUsers()) throw new RuntimeException("There is no loaded online users");
		return activeUsers;
	}

	private boolean hasLoadedOnlineUsers() {
		return onlineUsers != null;
	}

	private void clearUsersStatus() {
		activeUsers = null;
		onlineUsers = null;

		for (final HandlerRegistration reg : handlerRegistrations) {
			reg.removeHandler();
		}
		handlerRegistrations.clear();
	}

	public interface UsersStatusChangeListener {

		void onUsersStatusListUnavailable(Throwable caught);

		void onUsersStatusListsUpdated(SortedSet<UserRepresentation> activeUsers, SortedSet<UserRepresentation> onlineUsers);
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

	@Override
	public UserStatus getStatus(final UserRepresentation user) {
		if (getActiveUsers().contains(user)) return UserStatus.ACTIVE;
		if (getOnlineUsers().contains(user)) return UserStatus.ONLINE;
		return UserStatus.OFFLINE;
	}

	@Override
	public HandlerRegistration registerListenerForSpecificUser(final UserRepresentation user, final UserSpecificStatusChangeListener listener) {
		userSpecificListeners.put(user, listener);
		if (hasLoadedActiveUsers()) listener.onUserStatusChange(getStatus(user));

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				userSpecificListeners.remove(user, listener);
			}
		};
	}

	private void notifyUserSpecificStatusChangeListeners(final UserRepresentation user, final UserStatus status) {
		for (final UserSpecificStatusChangeListener listener : userSpecificListeners.get(user)) {
			listener.onUserStatusChange(status);
		}
	}
}
