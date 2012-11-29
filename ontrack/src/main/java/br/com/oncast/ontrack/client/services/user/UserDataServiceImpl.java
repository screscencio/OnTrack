package br.com.oncast.ontrack.client.services.user;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserDataServiceImpl implements UserDataService {

	private static final String GRAVATAR_BASE_URL = "https://secure.gravatar.com/";
	private final Map<String, PortableContactJsonObject> cachedResults;
	private final DispatchService dispatchService;
	private final Set<User> usersCached;
	private final Set<UserDataUpdateListener> listeners;

	public UserDataServiceImpl(final DispatchService dispatchService, final ContextProviderService contextProvider,
			final ServerPushClientService serverPushClientService) {
		this.dispatchService = dispatchService;

		listeners = new HashSet<UserDataUpdateListener>();
		usersCached = new HashSet<User>();
		cachedResults = new HashMap<String, PortableContactJsonObject>();

		contextProvider.addContextLoadListener(new ContextChangeListener() {

			@Override
			public void onProjectChanged(final UUID projectId) {
				updateUserDataFor(projectId);
			}
		});

		serverPushClientService.registerServerEventHandler(UserDataUpdateEvent.class, new ServerPushEventHandler<UserDataUpdateEvent>() {

			@Override
			public void onEvent(final UserDataUpdateEvent event) {
				usersCached.add(event.getUser());
				notifyUserDataUpdate(event.getUser());
			}
		});
	}

	@Override
	public SafeUri getAvatarUrl(final UserRepresentation userRepresentation) {
		return getAvatarUrl(retrieveRealUser(userRepresentation).getEmail());
	}

	@Override
	public void loadProfile(final String email, final LoadProfileCallback userNameCallback) {
		if (cachedResults.containsKey(email)) {
			userNameCallback.onProfileLoaded(cachedResults.get(email));
			return;
		}

		new JsonpRequestBuilder().requestObject(URL.encode(GRAVATAR_BASE_URL + getMd5Hex(email) + ".json"),
				new AsyncCallback<PortableContactJsonObject>() {

					@Override
					public void onFailure(final Throwable throwable) {
						userNameCallback.onProfileUnavailable(throwable);
					}

					@Override
					public void onSuccess(final PortableContactJsonObject result) {
						cachedResults.put(email, result);
						userNameCallback.onProfileLoaded(result);
					}

				});
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
		return null;
	}

	@Override
	public void onUserDataUpdate(final User user) {
		// TODO receber um callback? atualizar a view com o push event que vem?
		dispatchService.dispatch(new UserDataUpdateRequest(user), new DispatchCallback<UserDataUpdateRequestResponse>() {

			@Override
			public void onSuccess(final UserDataUpdateRequestResponse result) {
				// FIXME Auto-generated catch block

			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				// FIXME Auto-generated catch block

			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				// FIXME Auto-generated catch block

			}
		});
	}

	private void updateUserDataFor(final UUID projectId) {
		usersCached.clear();
		dispatchService.dispatch(new UserDataRequest(projectId), new DispatchCallback<UserDataRequestResponse>() {

			@Override
			public void onSuccess(final UserDataRequestResponse result) {
				usersCached.addAll(result.getUsers());
				notifyUserListLoaded(result.getUsers());
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

	private void notifyUserListLoaded(final List<User> users) {
		for (final UserDataUpdateListener listener : listeners)
			listener.onUserListLoaded(users);
	}

	private void notifyUserDataUpdate(final User user) {
		for (final UserDataUpdateListener listener : listeners)
			listener.onUserDataUpdate(user);
	}

	@Override
	public HandlerRegistration addUserDataUpdateListener(final UserDataUpdateListener listener) {
		listeners.add(listener);

		if (!usersCached.isEmpty()) listener.onUserListLoaded(new ArrayList<User>(usersCached));

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				listeners.remove(listener);
			}
		};
	}

	@Override
	public List<User> retrieveRealUsers(final List<UserRepresentation> users) {
		final List<User> responseUsers = new ArrayList<User>();
		for (final UserRepresentation userRepresentation : users) {
			final User user = retrieveRealUser(userRepresentation);
			if (user != null) responseUsers.add(user);
		}

		return responseUsers;
	}

	@Override
	public User retrieveRealUser(final UserRepresentation userRepresentation) {
		for (final User user : usersCached) {
			if (user.getId().equals(userRepresentation.getId())) return user;
		}
		return null;
	}

	@Override
	public SafeUri getAvatarUrl(final String email) {
		return new SafeUri() {
			@Override
			public String asString() {
				try {
					return new String(GRAVATAR_BASE_URL + "avatar/" + getMd5Hex(email) + "?s=40&d=mm");
				}
				catch (final Exception e) {
					return null;
				}
			}

		};
	}
}
