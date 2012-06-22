package br.com.oncast.ontrack.client.services.user;

import com.google.gwt.safehtml.shared.SafeUri;

public interface UserDataService {

	SafeUri getAvatarUrl(String email);

	void loadProfile(String email, LoadProfileCallback userNameCallback);

	public interface LoadProfileCallback {

		void onProfileLoaded(PortableContactJsonObject profile);

		void onProfileUnavailable(Throwable cause);

	}

}
