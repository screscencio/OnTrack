package br.com.oncast.ontrack.client.services.errorHandling;

import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService.ConfirmationListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;

public class ErrorTreatmentServiceImpl implements ErrorTreatmentService {

	public ErrorTreatmentServiceImpl() {
		setUpGlobalExceptionHandler();
	}

	@Override
	public void treatFatalError(final String errorDescriptionMessage, final Throwable caught) {
		treatFatalError(errorDescriptionMessage);
	}

	@Override
	public void treatFatalError(final String errorDescriptionMessage) {
		ClientNotificationService.showErrorWithConfirmation(errorDescriptionMessage, new ConfirmationListener() {
			@Override
			public void onConfirmation() {
				Window.Location.reload();
			}
		});
	}

	@Override
	public void treatConnectionError(final String errorDescriptionMessage, final Throwable caught) {
		Window.Location.reload();
		ClientNotificationService.showModalError(errorDescriptionMessage);
	}

	@Override
	public void treatUserWarning(final String message, final Exception e) {
		ClientNotificationService.showError(message);
	}

	private void setUpGlobalExceptionHandler() {
		// TODO Use the centralized exception handler.
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(final Throwable e) {
				e.printStackTrace(System.out);
				ClientNotificationService.showError(e.getMessage());
			}
		});
	}

}
