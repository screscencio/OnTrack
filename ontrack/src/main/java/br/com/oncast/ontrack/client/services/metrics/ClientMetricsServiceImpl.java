package br.com.oncast.ontrack.client.services.metrics;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerMetricsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerMetricsResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ClientMetricsServiceImpl implements ClientMetricsService {

	private final DispatchService dispatchService;

	private OnTrackStatisticsFactory factory;

	public ClientMetricsServiceImpl(final DispatchService requestDispatchService) {
		this.dispatchService = requestDispatchService;
	}

	@Override
	public void getMetrics(final AsyncCallback<OnTrackServerMetrics> callback) {
		dispatchService.dispatch(new OnTrackServerMetricsRequest(), new DispatchCallback<OnTrackServerMetricsResponse>() {
			@Override
			public void onSuccess(final OnTrackServerMetricsResponse result) {
				callback.onSuccess(result.getStatistics(getFactory()));
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	private OnTrackStatisticsFactory getFactory() {
		return (factory == null) ? factory = GWT.create(OnTrackStatisticsFactory.class) : factory;
	}

}
