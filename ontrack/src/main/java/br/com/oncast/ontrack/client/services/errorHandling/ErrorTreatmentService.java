package br.com.oncast.ontrack.client.services.errorHandling;

public interface ErrorTreatmentService {

	void threatFatalError(String errorDescriptionMessage, Throwable caught);

	void threatFatalError(String errorDescriptionMessage);

}
