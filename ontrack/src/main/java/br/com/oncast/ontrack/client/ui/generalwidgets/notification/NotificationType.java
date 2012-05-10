package br.com.oncast.ontrack.client.ui.generalwidgets.notification;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public enum NotificationType {
	WARNING(NotificationBackgroundImages.INSTANCE.warningBgImg()),
	ERROR(NotificationBackgroundImages.INSTANCE.errorBgImg()),
	SUCCESS(NotificationBackgroundImages.INSTANCE.successBgImg()),
	INFO(NotificationBackgroundImages.INSTANCE.infoBgImg());

	private final ImageResource resource;

	private NotificationType(final ImageResource resource) {
		this.resource = resource;
	}

	SafeUri getIconSafeUri() {
		return resource.getSafeUri();
	}
}
