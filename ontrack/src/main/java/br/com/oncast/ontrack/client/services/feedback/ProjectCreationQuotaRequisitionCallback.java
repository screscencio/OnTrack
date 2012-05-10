package br.com.oncast.ontrack.client.services.feedback;

public interface ProjectCreationQuotaRequisitionCallback {

	void onRequestSentSucessfully();

	void onUnexpectedFailure(Throwable caught);

}
