package br.com.oncast.ontrack.client.services.errorHandling;


public interface ErrorTreatmentService {

	void treatFatalError(String errorDescriptionMessage, Throwable caught);

	void treatFatalError(String errorDescriptionMessage);

	void treatUserWarning(String string, Exception e);
}
