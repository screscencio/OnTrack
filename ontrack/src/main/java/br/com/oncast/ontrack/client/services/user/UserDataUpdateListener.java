package br.com.oncast.ontrack.client.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.user.User;

public interface UserDataUpdateListener {

	void onUserDataUpdate(User user);

	void onUserListLoaded(List<User> users);

}
