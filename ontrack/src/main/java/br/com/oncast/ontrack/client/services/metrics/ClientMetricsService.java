package br.com.oncast.ontrack.client.services.metrics;

import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClientMetricsService {

	void getMetrics(AsyncCallback<OnTrackServerMetrics> callback);

}
