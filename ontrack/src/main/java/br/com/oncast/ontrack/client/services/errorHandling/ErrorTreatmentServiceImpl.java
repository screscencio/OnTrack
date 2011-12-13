package br.com.oncast.ontrack.client.services.errorHandling;

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
		Window.alert(errorDescriptionMessage);
		Window.Location.reload();
	}

	@Override
	public void treatUserWarning(final String message, final Exception e) {
		Window.alert(message);
	}

	private void setUpGlobalExceptionHandler() {
		// TODO Use the centralized exception handler.
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(final Throwable e) {
				Window.alert(e.getMessage());
			}
		});
	}
}
