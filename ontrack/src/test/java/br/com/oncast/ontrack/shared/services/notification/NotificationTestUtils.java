package br.com.oncast.ontrack.shared.services.notification;


public class NotificationTestUtils {

	public static Notification createImportantMail() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.IMPEDIMENT_CREATED);
		return notification;
	}

	public static Notification createNotImportantMail() {
		final Notification notification = new Notification();
		notification.setType(NotificationType.ANNOTATION_DEPRECATED);
		return notification;
	}

}
