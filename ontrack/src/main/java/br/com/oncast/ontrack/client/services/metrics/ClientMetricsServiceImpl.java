package br.com.oncast.ontrack.client.services.metrics;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackRealTimeServerMetricsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackRealTimeServerMetricsResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerStatisticsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.metrics.OnTrackServerStatisticsResponse;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import static br.com.oncast.ontrack.shared.metrics.MetricsCategories.PLACE_LOAD;

public class ClientMetricsServiceImpl implements ClientMetricsService, ActionExecutionListener {

	private static final int ACTIONS_DIMENSION = 1;

	private final DispatchService dispatchService;

	private OnTrackStatisticsFactory factory;

	public ClientMetricsServiceImpl(final DispatchService requestDispatchService, final ActionExecutionService actionExecutionService) {
		this.dispatchService = requestDispatchService;
		actionExecutionService.addActionExecutionListener(this);
	}

	@Override
	public void getRealTimeMetrics(final Date lastMetricsUpdate, final AsyncCallback<OnTrackRealTimeServerMetrics> callback) {
		dispatchService.dispatch(new OnTrackRealTimeServerMetricsRequest(lastMetricsUpdate), new DispatchCallback<OnTrackRealTimeServerMetricsResponse>() {
			@Override
			public void onSuccess(final OnTrackRealTimeServerMetricsResponse result) {
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

	@Override
	public void getServerStatistics(final AsyncCallback<OnTrackServerStatistics> callback) {
		dispatchService.dispatch(new OnTrackServerStatisticsRequest(), new DispatchCallback<OnTrackServerStatisticsResponse>() {
			@Override
			public void onSuccess(final OnTrackServerStatisticsResponse result) {
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
		GoogleAnalyticsNativeImpl.set("page", "/" + MetricsTokenizer.getClassSimpleName(place));
	}

	@Override
	public void onNewWindowPlaceRequest(final OpenInNewWindowPlace place) {
		if (!(place instanceof Place)) throw new IllegalArgumentException("obj should be subclass of Place");
		onPlaceRequest((Place) place);
		GoogleAnalyticsNativeImpl.trackPageview();
	}

	@Override
	public TimeTrackingEvent startTimeTracking(final MetricsCategories category, final String value) {
		return new TimeTrackingEvent(this, category.getCategory(), value);
	}

	@Override
	public TimeTrackingEvent startPlaceLoad(final Place place) {
		GoogleAnalyticsNativeImpl.trackPageview();
		return new TimeTrackingEvent(this, PLACE_LOAD.getCategory(), MetricsTokenizer.getClassSimpleName(place));
	}

	void onTimeTrackingEnd(final TimeTrackingEvent event) {
		GoogleAnalyticsNativeImpl.trackTiming(event.getCategory(), event.getValue(), event.getTotalDuration());
	}

	@Override
	public void onException(final String message) {
		GoogleAnalyticsNativeImpl.sendException(message);
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext, final boolean isUserAction) {
		if (isUserAction) GoogleAnalyticsNativeImpl.sendCustomDimension(ACTIONS_DIMENSION, MetricsTokenizer.getClassSimpleName(action));
	}

}
