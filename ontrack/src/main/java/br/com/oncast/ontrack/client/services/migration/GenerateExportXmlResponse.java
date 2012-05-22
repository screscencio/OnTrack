package br.com.oncast.ontrack.client.services.migration;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

public class GenerateExportXmlResponse implements DispatchResponse {

	private String text;

	public GenerateExportXmlResponse() {}

	public GenerateExportXmlResponse(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
