package br.com.oncast.ontrack.client.services.notification;

import br.com.oncast.ontrack.client.ui.generalwidgets.ErrorMaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.HideHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.notification.Notification;
import br.com.oncast.ontrack.client.ui.generalwidgets.notification.NotificationContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.notification.NotificationType;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor this class to be more flexible or create a builder
public class ClientNotificationService {

	private static final int DURATION_SHORT = 3000;

	private static final int DURATION_LONG = 5000;

	Widget notificationParentPanel;

	NotificationContainer notificationContainer;

	public ClientNotificationService() {
		notificationContainer = new NotificationContainer();
	}

	/**
	 * Set the Notification's ParentWidget, useful to set the notification's position or from where it will appear
	 * @param the new parent widget
	 * @return the previous parent widget
	 */
	public Widget setNotificationParentWidget(final Widget widget) {
		final Widget previous = clearNotificationParentWidget();
		notificationParentPanel = widget;
		notificationParentPanel.getElement().appendChild(notificationContainer.getElement());
		return previous;
	}

	public Widget clearNotificationParentWidget() {
		if (notificationParentPanel == null) return null;

		final Widget widget = notificationParentPanel;

		notificationContainer.removeFromParent();
		notificationParentPanel = null;
		return widget;
	}

	private void addNotificationToNotificationContainer(final Notification notificationMessage) {
		notificationContainer.add(notificationMessage);
	}

	private void removeNotificationFromNotificationContainer(final Notification notificationMessage) {
		notificationContainer.remove(notificationMessage);
	}

	// FIXME MATS - Mat, as chamadas para este metodo são os que devem se tornar popUps como o que a gente viu com o Gmail. Não sei se o lugar a implementar
	// esse popUp seria nesta classe, mas mesmo que sim não deveria mais usar o esquema com a notificação existente.
	public void showErrorWithConfirmation(final String errorMessage, final NotificationConfirmationListener confirmationListener) {
		makeConfirmationNotification(errorMessage, NotificationType.ERROR, confirmationListener);
	}

	public void showModalError(final String errorDescriptionMessage) {
		makeModalAutoCloseNotification(errorDescriptionMessage, NotificationType.ERROR, DURATION_LONG);
	}

	public void showLongDurationInfo(final String message) {
		makeAutoCloseNotification(message, NotificationType.INFO, DURATION_LONG);
	}

	public void showInfo(final String message) {
		makeAutoCloseNotification(message, NotificationType.INFO, DURATION_SHORT);
	}

	public void showError(final String message) {
		makeAutoCloseNotification(message, NotificationType.ERROR, DURATION_LONG);
	}

	public void showWarning(final String message) {
		makeAutoCloseNotification(message, NotificationType.WARNING, DURATION_SHORT);
	}

	public void showSuccess(final String message) {
		makeAutoCloseNotification(message, NotificationType.SUCCESS, DURATION_SHORT);
	}

	private void makeAutoCloseNotification(final String message, final NotificationType type, final int autoCloseTime) {
		final Notification toast = new Notification();
		addNotificationToNotificationContainer(toast);

		toast.show(message, type, new AnimationCallback() {

			@Override
			public void onComplete() {
				new Timer() {
					@Override
					public void run() {
						toast.hide(new AnimationCallback() {

							@Override
							public void onComplete() {
								removeNotificationFromNotificationContainer(toast);
							}
						});
					}
				}.schedule(autoCloseTime);
			}
		});
	}

	private void makeConfirmationNotification(final String message, final NotificationType type, final NotificationConfirmationListener listener) {
		final Notification toast = new Notification();
		addNotificationToNotificationContainer(toast);

		ErrorMaskPanel.show(new HideHandler() {

			@Override
			public void onWillHide() {
				toast.hide(new AnimationCallback() {

					@Override
					public void onComplete() {
						removeNotificationFromNotificationContainer(toast);
						listener.onConfirmation();
					}
				});
			}
		});
		toast.show(message, type);
	}

	private void makeModalAutoCloseNotification(final String errorDescriptionMessage, final NotificationType type, final int autoCloseTime) {
		final Notification toast = new Notification();
		addNotificationToNotificationContainer(toast);

		ErrorMaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				toast.hide(new AnimationCallback() {

					@Override
					public void onComplete() {
						removeNotificationFromNotificationContainer(toast);
					}
				});
			}
		});
		toast.show(errorDescriptionMessage, type, new AnimationCallback() {

			@Override
			public void onComplete() {
				new Timer() {
					@Override
					public void run() {
						MaskPanel.assureHidden();
					}
				}.schedule(autoCloseTime);
			}
		});
	}
}
