package br.com.oncast.ontrack.shared.services.user;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface UserStatusEvent extends ServerPushEvent {

	String getUserEmail();

}
