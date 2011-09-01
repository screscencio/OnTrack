package br.com.oncast.ontrack.client.services.errorHandling;

import com.google.gwt.user.client.Window;

public class ErrorTreatmentServiceImpl implements ErrorTreatmentService {

	@Override
	public void threatFatalError(final String errorDescriptionMessage, final Throwable caught) {
		caught.printStackTrace();
		threatFatalError(errorDescriptionMessage);
	}

	@Override
	public void threatFatalError(final String errorDescriptionMessage) {
		Window.alert(errorDescriptionMessage);
		Window.Location.reload();
	}

}
