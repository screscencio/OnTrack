package br.com.oncast.ontrack.client.ui.generalwidgets.alerting;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public enum AlertType {
	WARNING(AlertingBackgroundImages.INSTANCE.warningBgImg()),
	ERROR(AlertingBackgroundImages.INSTANCE.errorBgImg()),
	SUCCESS(AlertingBackgroundImages.INSTANCE.successBgImg()),
	INFO(AlertingBackgroundImages.INSTANCE.infoBgImg());

	private final ImageResource resource;

	private AlertType(final ImageResource resource) {
		this.resource = resource;
	}

	SafeUri getIconSafeUri() {
		return resource.getSafeUri();
	}
}
