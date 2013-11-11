package br.com.oncast.ontrack.client.services.metrics;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;

import java.util.Date;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClientMetricsService {

	void getRealTimeMetrics(Date lastMetricsUpdate, AsyncCallback<OnTrackRealTimeServerMetrics> callback);

	void onPlaceRequest(Place place);

	TimeTrackingEvent startPlaceLoad(Place place);

	TimeTrackingEvent startTimeTracking(MetricsCategories category, String eventName);

	void onException(String message);

	void onNewWindowPlaceRequest(OpenInNewWindowPlace place);

	void getServerStatistics(AsyncCallback<OnTrackServerStatistics> asyncCallback);

}
