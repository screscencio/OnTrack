package br.com.oncast.ontrack.client.services.errorHandling;

import junit.framework.Assert;

public class ErrorTreatmentMock implements ErrorTreatmentService {

	@Override
	public void treatFatalError(final String errorDescriptionMessage, final Throwable caught) {
		Assert.fail("Error: " + errorDescriptionMessage + " - " + caught.getMessage());
		caught.printStackTrace();
	}

	@Override
	public void treatFatalError(final String errorDescriptionMessage) {
		Assert.fail("Error: " + errorDescriptionMessage);
	}

	@Override
	public void treatUserWarning(final String string, final Exception e) {
		throw new RuntimeException(e);
	}

}
