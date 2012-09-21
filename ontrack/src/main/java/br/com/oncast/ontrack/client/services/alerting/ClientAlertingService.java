package br.com.oncast.ontrack.client.services.alerting;

import br.com.oncast.ontrack.client.ui.generalwidgets.ErrorMaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.HideHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.Alert;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.AlertingContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.AlertType;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor this class to be more flexible or create a builder
public class ClientAlertingService {

	private static final int DURATION_SHORT = 3000;

	private static final int DURATION_LONG = 5000;

	Widget alertingParentPanel;

	AlertingContainer alertingContainer;

	public ClientAlertingService() {
		alertingContainer = new AlertingContainer();
	}

	/**
	 * Set the Alerting's ParentWidget, useful to set the alert's position or from where it will appear
	 * @param the new parent widget
	 * @return the previous parent widget
	 */
	public Widget setAlertingParentWidget(final Widget widget) {
		final Widget previous = clearAlertingParentWidget();
		alertingParentPanel = widget;
		alertingParentPanel.getElement().appendChild(alertingContainer.getElement());
		return previous;
	}

	public Widget clearAlertingParentWidget() {
		if (alertingParentPanel == null) return null;

		final Widget widget = alertingParentPanel;

		alertingContainer.removeFromParent();
		alertingParentPanel = null;
		return widget;
	}

	private void addAlertToAlertingContainer(final Alert alertMessage) {
		alertingContainer.add(alertMessage);
	}

	private void removeAlertFromAlertingContainer(final Alert alertingMessage) {
		alertingContainer.remove(alertingMessage);
	}

	// FIXME MATS - Mat, as chamadas para este metodo são os que devem se tornar popUps como o que a gente viu com o Gmail. Não sei se o lugar a implementar
	// esse popUp seria nesta classe, mas mesmo que sim não deveria mais usar o esquema com a notificação existente.
	public void showErrorWithConfirmation(final String errorMessage, final AlertConfirmationListener confirmationListener) {
		makeConfirmationAlert(errorMessage, AlertType.ERROR, confirmationListener);
	}

	public void showModalError(final String errorDescriptionMessage) {
		makeModalAutoCloseAlert(errorDescriptionMessage, AlertType.ERROR, DURATION_LONG);
	}

	public void showLongDurationInfo(final String message) {
		makeAutoCloseAlert(message, AlertType.INFO, DURATION_LONG);
	}

	public void showInfo(final String message) {
		makeAutoCloseAlert(message, AlertType.INFO, DURATION_SHORT);
	}

	public void showError(final String message) {
		makeAutoCloseAlert(message, AlertType.ERROR, DURATION_LONG);
	}

	public void showWarning(final String message) {
		makeAutoCloseAlert(message, AlertType.WARNING, DURATION_SHORT);
	}

	public void showSuccess(final String message) {
		makeAutoCloseAlert(message, AlertType.SUCCESS, DURATION_SHORT);
	}

	private void makeAutoCloseAlert(final String message, final AlertType type, final int autoCloseTime) {
		final Alert toast = new Alert();
		addAlertToAlertingContainer(toast);

		toast.show(message, type, new AnimationCallback() {

			@Override
			public void onComplete() {
				new Timer() {
					@Override
					public void run() {
						toast.hide(new AnimationCallback() {

							@Override
							public void onComplete() {
								removeAlertFromAlertingContainer(toast);
							}
						});
					}
				}.schedule(autoCloseTime);
			}
		});
	}

	private void makeConfirmationAlert(final String message, final AlertType type, final AlertConfirmationListener listener) {
		final Alert toast = new Alert();
		addAlertToAlertingContainer(toast);

		ErrorMaskPanel.show(new HideHandler() {

			@Override
			public void onWillHide() {
				toast.hide(new AnimationCallback() {

					@Override
					public void onComplete() {
						removeAlertFromAlertingContainer(toast);
						listener.onConfirmation();
					}
				});
			}
		});
		toast.show(message, type);
	}

	private void makeModalAutoCloseAlert(final String errorDescriptionMessage, final AlertType type, final int autoCloseTime) {
		final Alert toast = new Alert();
		addAlertToAlertingContainer(toast);

		ErrorMaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				toast.hide(new AnimationCallback() {

					@Override
					public void onComplete() {
						removeAlertFromAlertingContainer(toast);
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
