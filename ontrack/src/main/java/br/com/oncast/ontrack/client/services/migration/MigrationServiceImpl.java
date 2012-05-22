package br.com.oncast.ontrack.client.services.migration;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

public class MigrationServiceImpl implements MigrationService {

	private final DispatchService dispatchService;

	public MigrationServiceImpl(final DispatchService dispatchService) {
		this.dispatchService = dispatchService;
	}

	@Override
	public void generateXml(final GenerateXmlCallback callback) {
		dispatchService.dispatch(new GenerateExportXmlRequest(),
				new DispatchCallback<GenerateExportXmlResponse>() {

					@Override
					public void onSuccess(final GenerateExportXmlResponse result) {
						callback.onXmlGenerationSuccess(result.getText());
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {
						callback.onXmlGenerationFailure(caught);
					}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						callback.onXmlGenerationFailure(caught);
					}

				});

	}

}
