package br.com.oncast.ontrack.client.services.user;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataRequestResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataUpdateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataUpdateRequestResponse;
import br.com.oncast.ontrack.shared.services.user.UserDataUpdateEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserDataServiceImpl implements UserDataService {

	private static final String GRAVATAR_BASE_URL = "https://secure.gravatar.com/";

	private final DispatchService dispatchService;

	private final Set<User> cachedUsers;
	private final SetMultimap<UUID, UserSpecificInformationChangeListener> userSpecificListeners;

	public UserDataServiceImpl(final DispatchService dispatchService, final ContextProviderService contextProvider,
			final ServerPushClientService serverPushClientService) {

		this.dispatchService = dispatchService;

		userSpecificListeners = HashMultimap.create();
		cachedUsers = new HashSet<User>();

		contextProvider.addContextLoadListener(new ContextChangeListener() {
			@Override
			public void onProjectChanged(final UUID projectId) {
				updateUserDataFor(projectId);
			}
		});

		serverPushClientService.registerServerEventHandler(UserDataUpdateEvent.class, new ServerPushEventHandler<UserDataUpdateEvent>() {
			@Override
			public void onEvent(final UserDataUpdateEvent event) {
				final User user = event.getUser();
				cachedUsers.add(user);
				notifyUserDataUpdate(user);
			}
		});
	}

	@Override
	public SafeUri getAvatarUrl(final User user) {
		return new SafeUri() {
			@Override
			public String asString() {
				return new String(GRAVATAR_BASE_URL + "avatar/" + getMd5Hex(user.getEmail()) + "?s=40&d=mm");
			}

		};
	}

	private String getMd5Hex(final String email) {
		try {
			final BigInteger hash = new BigInteger(1, MessageDigest.getInstance("MD5").digest(email.trim().toLowerCase().getBytes()));
			final String md5 = hash.toString(16);
			return md5;
		}
		catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void onUserDataUpdate(final User user, final AsyncCallback<User> callback) {
		dispatchService.dispatch(new UserDataUpdateRequest(user), new DispatchCallback<UserDataUpdateRequestResponse>() {
			@Override
			public void onSuccess(final UserDataUpdateRequestResponse result) {
				callback.onSuccess(result.getUser());
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

	private void updateUserDataFor(final UUID projectId) {
		cachedUsers.clear();
		dispatchService.dispatch(new UserDataRequest(projectId), new DispatchCallback<UserDataRequestResponse>() {

			@Override
			public void onSuccess(final UserDataRequestResponse result) {
				final List<User> users = result.getUsers();
				cachedUsers.addAll(users);
				notifyAllListeners(users);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				caught.printStackTrace();
			}

		});
	}

	private void notifyUserDataUpdate(final User user) {
		for (final UserSpecificInformationChangeListener listener : userSpecificListeners.get(user.getId()))
			listener.onInformationChange(user);
	}

	private void notifyAllListeners(final List<User> users) {
		for (final User user : users)
			notifyUserDataUpdate(user);
	}

	@Override
	public User retrieveRealUser(final UserRepresentation userRepresentation) {
		for (final User user : cachedUsers) {
			if (user.getId().equals(userRepresentation.getId())) return user;
		}
		throw new IllegalStateException("User information unavailable");
	}

	@Override
	public HandlerRegistration registerListenerForSpecificUser(final UserRepresentation user, final UserSpecificInformationChangeListener listener) {
		userSpecificListeners.put(user.getId(), listener);
		if (cachedUsers.contains(user)) listener.onInformationChange(retrieveRealUser(user));

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				userSpecificListeners.remove(user.getId(), listener);
			}
		};
	}

	public interface UserSpecificInformationChangeListener {

		void onInformationChange(User user);

	}
}
