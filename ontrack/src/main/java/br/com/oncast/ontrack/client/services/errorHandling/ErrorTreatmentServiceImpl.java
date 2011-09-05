package br.com.oncast.ontrack.client.services.errorHandling;

import com.google.gwt.user.client.Window;

public class ErrorTreatmentServiceImpl implements ErrorTreatmentService {

	@Override
	public void treatFatalError(final String errorDescriptionMessage, final Throwable caught) {
		caught.printStackTrace();
		treatFatalError(errorDescriptionMessage);
	}

	@Override
	public void treatFatalError(final String errorDescriptionMessage) {
		Window.alert(errorDescriptionMessage);
		Window.Location.reload();
	}

	@Override
	public void treatUserWarning(final String message, final Exception e) {
		new RuntimeException(e);
	}
}
