package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;

public interface NewNotificationEventHandler extends ServerPushEventHandler<NotificationCreatedEvent> {}
