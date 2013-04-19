package br.com.oncast.ontrack.client.services.metrics;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClientMetricsService {

	void getMetrics(AsyncCallback<OnTrackServerMetrics> callback);

	void onPlaceRequest(Place place);

	TimeTrackingEvent startPlaceLoad(Place place);

	TimeTrackingEvent startTimeTracking(MetricsCategories category, String eventName);

	void onException(Exception e);

	void onUserLogin(User user);

	void onUserLogout();

	void onNewWindowPlaceRequest(OpenInNewWindowPlace place);

}
