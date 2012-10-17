package br.com.oncast.ontrack.client.services.notification;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationReadStateChangeListener {

	void readStateChanged(Notification notification, boolean readState);

}
