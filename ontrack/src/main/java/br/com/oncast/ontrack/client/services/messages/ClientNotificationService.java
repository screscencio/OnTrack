package br.com.oncast.ontrack.client.services.messages;

import br.com.oncast.ontrack.client.ui.generalwidgets.ErrorMaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.HideHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.NotificationWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.NotificationWidget.NotificationType;

import com.google.gwt.user.client.Timer;

// TODO Refactor this class to be more flexible or create a builder
public class ClientNotificationService {

	public static void showErrorWithConfirmation(final String errorMessage, final ConfirmationListener confirmationListener) {
		makeConfirmationNotification(errorMessage, NotificationType.ERROR, confirmationListener);
	}

	public static void showModalError(final String errorDescriptionMessage) {
		makeModalAutoCloseNotification(errorDescriptionMessage, NotificationType.ERROR, 5000);
	}

	public static void showError(final String errorMessage) {
		makeAutoCloseNotification(errorMessage, NotificationType.WARNING, 5000);
	}

	public static void showMessage(final String message) {
		makeAutoCloseNotification(message, NotificationType.SUCCESS, 3000);

	}

	// FIXME handle clicks over the toast
	private static void makeConfirmationNotification(final String message, final NotificationType type, final ConfirmationListener listener) {
		final NotificationWidget toast = new NotificationWidget();
		ErrorMaskPanel.show(new HideHandler() {

			@Override
			public void onWillHide() {
				toast.hide();
				listener.onConfirmation();
			}
		});
		toast.show(message + " Please Click Anyware", type);
	}

	private static void makeAutoCloseNotification(final String message, final NotificationType type, final int autoCloseTime) {
		final NotificationWidget toast = new NotificationWidget();

		toast.show(message, type);
		new Timer() {
			@Override
			public void run() {
				toast.hide();
			}
		}.schedule(autoCloseTime);
	}

	private static void makeModalAutoCloseNotification(final String errorDescriptionMessage, final NotificationType type, final int autoCloseTime) {
		final NotificationWidget toast = new NotificationWidget();
		ErrorMaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				toast.hide();
			}
		});
		toast.show(errorDescriptionMessage, type);
		new Timer() {
			@Override
			public void run() {
				MaskPanel.assureHidden();
			}
		}.schedule(autoCloseTime);
	}

	public interface ConfirmationListener {
		public void onConfirmation();
	}

}
