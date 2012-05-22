package br.com.oncast.ontrack.client.services.migration;

public interface GenerateXmlCallback {

	void onXmlGenerationSuccess(String text);

	void onXmlGenerationFailure(Throwable caught);

}
