package br.com.oncast.ontrack.client.services.metrics;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerMetricsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerMetricsResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import static br.com.oncast.ontrack.shared.metrics.MetricsCategories.PLACE_LOAD;

public class ClientMetricsServiceImpl implements ClientMetricsService {

	private final DispatchService dispatchService;

	private OnTrackStatisticsFactory factory;

	private String trackerPrefix = "";

	private Place place = null;

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

	@Override
	public void onPlaceRequest(final Place place) {
		this.place = place;
		GoogleAnalytics.set(trackerPrefix, "page", "/" + MetricsTokenizer.getClassSimpleName(place));
	}

	@Override
	public void onNewWindowPlaceRequest(final OpenInNewWindowPlace place) {
		if (!(place instanceof Place)) throw new IllegalArgumentException("obj should be subclass of Place");
		onPlaceRequest((Place) place);
		GoogleAnalytics.trackPageview(trackerPrefix);
	}

	@Override
	public TimeTrackingEvent startTimeTracking(final MetricsCategories category, final String value) {
		return new TimeTrackingEvent(this, category.getCategory(), value);
	}

	@Override
	public TimeTrackingEvent startPlaceLoad(final Place place) {
		GoogleAnalytics.trackPageview(trackerPrefix);
		return new TimeTrackingEvent(this, PLACE_LOAD.getCategory(), MetricsTokenizer.getClassSimpleName(place));
	}

	void onTimeTrackingEnd(final TimeTrackingEvent event) {
		GoogleAnalytics.trackTiming(trackerPrefix, event.getCategory(), event.getValue(), event.getTotalDuration());
	}

	@Override
	public void onException(final String message) {
		GoogleAnalytics.sendException(trackerPrefix, message);
	}

	@Override
	public void onUserLogin(final User user) {
		final String userId = user.getId().toString();
		trackerPrefix = userId.replaceAll("[^a-zA-Z0-9]+", "");
		GoogleAnalytics.create(trackerPrefix, userId);
		trackerPrefix += ".";
		updateCurrentPage();
	}

	@Override
	public void onUserLogout() {
		trackerPrefix = "";
		updateCurrentPage();
	}

	private void updateCurrentPage() {
		if (place == null) return;
		GoogleAnalytics.set(trackerPrefix, "page", "/" + MetricsTokenizer.getClassSimpleName(place));
	}

}
