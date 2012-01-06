package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.NotificationMole;
import com.google.gwt.user.client.ui.RootPanel;

public class NotificationWidget extends Composite {

	private final NotificationMole notification;

	public NotificationWidget() {
		notification = new NotificationMole();
		RootPanel.get().add(notification);
		final Style notificationStyle = notification.getElement().getStyle();
		notificationStyle.setTop(0, Unit.PX);
		notificationStyle.setZIndex(1);
		final Style containerStyle = getContainerStyle();
		containerStyle.setBorderColor("#585E76");
		containerStyle.setWidth(331, Unit.PX);
		containerStyle.setPaddingLeft(43, Unit.PX);
		containerStyle.setProperty("background", "#FFF -8px 0px no-repeat");
		containerStyle.setProperty("backgroundSize", 50, Unit.PX);
	}

	public void hide() {
		notification.hide();
	}

	public void show(final String message, final NotificationType type) {
		setBackground(type);
		notification.show(message);
	}

	private void setBackground(final NotificationType type) {
		getContainerStyle()
				.setBackgroundImage("url('" + type.getIconPath() + "')");
	}

	private Style getContainerStyle() {
		return notification.getElement().getChild(0).getChild(0).getParentElement().getStyle();
	}

	public enum NotificationType {
		WARNING("warning.png"),
		ERROR("error.png"),
		SUCCESS("done.png");

		public static final String ICON_PATH_PREFIX = "resources/image/notification/";
		private final String iconName;

		private NotificationType(final String iconFile) {
			this.iconName = iconFile;
		}

		String getIconPath() {
			return ICON_PATH_PREFIX + iconName;
		}
	}

}
