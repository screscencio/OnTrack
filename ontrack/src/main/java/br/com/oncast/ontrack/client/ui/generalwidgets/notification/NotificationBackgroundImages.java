package br.com.oncast.ontrack.client.ui.generalwidgets.notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface NotificationBackgroundImages extends ClientBundle {

	static NotificationBackgroundImages INSTANCE = GWT.create(NotificationBackgroundImages.class);

	@Source("error_bg.png")
	ImageResource errorBgImg();

	@Source("info_bg.png")
	ImageResource infoBgImg();

	@Source("success_bg.png")
	ImageResource successBgImg();

	@Source("warning_bg.png")
	ImageResource warningBgImg();
}
