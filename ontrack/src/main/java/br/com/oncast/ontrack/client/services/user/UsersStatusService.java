package br.com.oncast.ontrack.client.services.user;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UsersStatusService {

	void getActiveUsers(AsyncCallback<Set<User>> callback);

}
