package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;

public interface UserDataService {

	void loadProfile(String email, LoadProfileCallback userNameCallback);

	public interface LoadProfileCallback {

		void onProfileLoaded(PortableContactJsonObject profile);

		void onProfileUnavailable(Throwable cause);

	}

	void onUserDataUpdate(User user);

	List<User> retrieveRealUsers(List<UserRepresentation> users);

	User retrieveRealUser(UserRepresentation user);

	HandlerRegistration addUserDataUpdateListener(UserDataUpdateListener listener);

	SafeUri getAvatarUrl(UserRepresentation userRepresentation);

	SafeUri getAvatarUrl(String email);
}
