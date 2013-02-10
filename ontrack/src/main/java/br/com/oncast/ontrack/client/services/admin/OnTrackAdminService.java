package br.com.oncast.ontrack.client.services.admin;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsResponse;

public class OnTrackAdminService {

	private final DispatchService dispatchService;

	public OnTrackAdminService(final DispatchService requestDispatchService) {
		this.dispatchService = requestDispatchService;
	}

	public void getStatistics(final DispatchCallback<OnTrackServerStatisticsResponse> callback) {
		dispatchService.dispatch(new OnTrackServerStatisticsRequest(), callback);
	}
}
