package br.com.oncast.ontrack.client.services.user;

import java.util.SortedSet;

import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UserSpecificStatusChangeListener;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

import com.google.gwt.event.shared.HandlerRegistration;

public interface UsersStatusService {

	HandlerRegistration register(UsersStatusChangeListener usersStatusChangeListener);

	SortedSet<UserRepresentation> getActiveUsers();

	SortedSet<UserRepresentation> getOnlineUsers();

	UserStatus getStatus(UserRepresentation userRepresentation);

	HandlerRegistration registerListenerForSpecificUser(UserRepresentation user, UserSpecificStatusChangeListener listener);

}
