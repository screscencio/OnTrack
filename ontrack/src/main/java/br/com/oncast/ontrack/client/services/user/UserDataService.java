package br.com.oncast.ontrack.client.services.user;

import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl.UserSpecificInformationChangeListener;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserDataService {

	User retrieveRealUser(UserRepresentation user);

	SafeUri getAvatarUrl(User user);

	HandlerRegistration registerListenerForSpecificUser(UserRepresentation user, UserSpecificInformationChangeListener listener);

	void onUserDataUpdate(User user, AsyncCallback<User> callback);
}
