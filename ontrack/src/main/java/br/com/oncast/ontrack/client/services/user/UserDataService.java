package br.com.oncast.ontrack.client.services.user;

import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserDataService {

	User retrieveRealUser(UserRepresentation user);

	void loadRealUser(UUID userId, AsyncCallback<User> callback);

	SafeUri getAvatarUrl(User user);

	SafeUri getAvatarUrl(User user, int imageSize);

	HandlerRegistration registerListenerForSpecificUser(UserRepresentation user, UserSpecificInformationChangeListener listener);

	HandlerRegistration registerListenerForSpecificUser(UUID userId, UserSpecificInformationChangeListener listener);

	void onUserDataUpdate(User user, AsyncCallback<User> callback);

	void hasAvatarInGravatar(User user, UserHasGravatarCallback callback);

}
