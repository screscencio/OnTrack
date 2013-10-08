package br.com.oncast.ontrack.client.services.alerting;

import br.com.oncast.ontrack.client.ui.generalwidgets.ErrorMaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.HideHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MaskPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.Alert;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.AlertType;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.AlertWithButton;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.AlertingContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.alerting.BasicAlert;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;

// TODO Refactor this class to be more flexible or create a builder
public class ClientAlertingService {

	public static final int DURATION_SHORT = 3000;

	public static final int DURATION_LONG = 5000;

	Panel alertingParentPanel;

	AlertingContainer alertingContainer;

	public ClientAlertingService() {
		alertingContainer = new AlertingContainer();
	}

	/**
	 * Set the Alerting's ParentWidget, useful to set the alert's position or from where it will appear
	 * 
	 * @param the
	 *            new parent widget
	 * @return the previous parent widget
	 */
	public Panel setAlertingParentWidget(final Panel widget) {
		final Panel previous = clearAlertingParentWidget();
		alertingParentPanel = widget;
		alertingParentPanel.add(alertingContainer);
		return previous;
	}

	public Panel clearAlertingParentWidget() {
		if (alertingParentPanel == null) return null;

		final Panel widget = alertingParentPanel;

		alertingContainer.removeFromParent();
		alertingParentPanel = null;
		return widget;
	}

	private void addAlertToAlertingContainer(final Alert alertMessage) {
		alertingContainer.add(alertMessage.asWidget());
	}

	private void removeAlertFromAlertingContainer(final Alert alertingMessage) {
		alertingContainer.remove(alertingMessage.asWidget());
	}

	// TODO make this message alert like modal popup
	public ConfirmationAlertRegister showErrorWithConfirmation(final String errorMessage, final AlertConfirmationListener confirmationListener) {
		return makeConfirmationAlert(errorMessage, AlertType.ERROR, confirmationListener);
	}

	public void showModalError(final String errorDescriptionMessage) {
		makeModalAutoCloseAlert(errorDescriptionMessage, AlertType.ERROR, DURATION_LONG);
	}

	public AlertRegistration showLongDurationInfo(final String message) {
		return makeAutoCloseAlert(message, AlertType.INFO, DURATION_LONG);
	}

	public AlertRegistration showInfo(final String message) {
		return makeAutoCloseAlert(message, AlertType.INFO, DURATION_SHORT);
	}

	public AlertRegistration showError(final String message) {
		return makeAutoCloseAlert(message, AlertType.ERROR, DURATION_LONG);
	}

	public AlertRegistration showWarning(final String message) {
		return makeAutoCloseAlert(message, AlertType.WARNING, DURATION_SHORT);
	}

	public AlertRegistration showWarning(final String message, final int duration) {
		return makeAutoCloseAlert(message, AlertType.WARNING, duration);
	}

	public AlertRegistration showInfoWithButton(final String message, final String buttonText, final ClickHandler handler) {
		final AlertWithButton toast = new AlertWithButton();
		toast.setButtonText(buttonText);
		toast.addClickHandler(handler);
		return makeAutoCloseAlert(toast, message, AlertType.INFO, DURATION_LONG);
	}

	public AlertRegistration showSuccess(final String message) {
		return showSuccess(message, DURATION_SHORT);
	}

	public AlertRegistration showSuccess(final String message, final int duration) {
		return makeAutoCloseAlert(message, AlertType.SUCCESS, duration);
	}

	public AlertRegistration showBlockingError(final String message) {
		return makeBlockingAlert(message, AlertType.ERROR);
	}

	private AlertRegistration makeBlockingAlert(final String message, final AlertType type) {
		final BasicAlert toast = new BasicAlert();
		final AlertRegistration alertRegistration = register(toast);
		toast.show(message, type);
		return alertRegistration;
	}

	private AlertRegistration makeAutoCloseAlert(final String message, final AlertType type, final int autoCloseTime) {
		return makeAutoCloseAlert(new BasicAlert(), message, type, autoCloseTime);
	}

	private AlertRegistration makeAutoCloseAlert(final Alert toast, final String message, final AlertType type, final int autoCloseTime) {
		final AlertRegistration alertRegistration = register(toast);
		toast.show(message, type, new AnimationCallback() {
			@Override
			public void onComplete() {
				new Timer() {
					@Override
					public void run() {
						alertRegistration.hide();
					}
				}.schedule(autoCloseTime);
			}
		});
		return alertRegistration;
	}

	private ConfirmationAlertRegister makeConfirmationAlert(final String message, final AlertType type, final AlertConfirmationListener listener) {
		final BasicAlert toast = new BasicAlert();
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

		return new ConfirmationAlertRegister() {
			@Override
			public void hide(final boolean confirmation) {
				ErrorMaskPanel.assureHidden();
				removeAlertFromAlertingContainer(toast);
				if (confirmation) listener.onConfirmation();
			}
		};
	}

	private void makeModalAutoCloseAlert(final String errorDescriptionMessage, final AlertType type, final int autoCloseTime) {
		final BasicAlert toast = new BasicAlert();
		addAlertToAlertingContainer(toast);

		ErrorMaskPanel.show(new HideHandler() {
			@Override
			public void onWillHide() {
				hideAndRemoveFromContainer(toast);
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

	private AlertRegistration register(final Alert toast) {
		addAlertToAlertingContainer(toast);
		final AlertRegistration alertRegistration = new AlertRegistration() {
			@Override
			public void hide() {
				hideAndRemoveFromContainer(toast);
			}

			@Override
			public void setMessage(final String message) {
				toast.setMessage(message);
			}
		};
		return alertRegistration;
	}

	private void hideAndRemoveFromContainer(final Alert toast) {
		toast.hide(new AnimationCallback() {
			@Override
			public void onComplete() {
				removeAlertFromAlertingContainer(toast);
			}
		});
	}

}
