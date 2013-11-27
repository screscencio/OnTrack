package br.com.oncast.ontrack.client.services.metrics;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
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

public class ClientMetricsServiceImpl implements ClientMetricsService {

	private static final int DIMENSION_USER = 1;

	private static final int DIMENSION_PROJECT = 2;

	private static final String ANONYMOUS_USER = "Anonymous";

	private static final String NO_PROJECT = "No Project";

	private final DispatchService dispatchService;

	private OnTrackStatisticsFactory factory;

	public ClientMetricsServiceImpl(final DispatchService requestDispatchService) {
		this.dispatchService = requestDispatchService;
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
		GoogleAnalyticsNativeImpl.set("page", "/" + className(place));
	}

	@Override
	public void onNewWindowPlaceRequest(final OpenInNewWindowPlace place) {
		if (!(place instanceof Place)) throw new IllegalArgumentException("the palce argument should be subclass of Place");
		onPlaceRequest((Place) place);
		GoogleAnalyticsNativeImpl.trackPageview();
	}

	@Override
	public TimeTrackingEvent startTimeTracking(final MetricsCategories category, final String value) {
		return new TimeTrackingEvent(this, category.getCategory(), value);
	}

	@Override
	public TimeTrackingEvent startPlaceLoad(final Place place) {
		return startPlaceLoad(place.getClass());
	}

	@Override
	public TimeTrackingEvent startPlaceLoad(final Class<? extends Place> placeType) {
		GoogleAnalyticsNativeImpl.trackPageview();
		return new TimeTrackingEvent(this, PLACE_LOAD.getCategory(), className(placeType));
	}

	void onTimeTrackingEnd(final TimeTrackingEvent event) {
		GoogleAnalyticsNativeImpl.trackTiming(event.getCategory(), event.getLabel(), event.getTotalDuration());
	}

	@Override
	public void onException(final String message) {
		GoogleAnalyticsNativeImpl.sendException(message);
	}

	@Override
	public void onActionExecution(final UserAction action, final boolean isClientOnline) {
		if (isClientOnline) GoogleAnalyticsNativeImpl.sendEvent("action", "client_side_execution", className(action.getModelAction()));
		else GoogleAnalyticsNativeImpl.sendEvent("action", "offline_execution", className(action.getModelAction()));
	}

	@Override
	public void onActionConflict(final UserAction action, final UnableToCompleteActionException e) {
		GoogleAnalyticsNativeImpl.sendEvent("action", "client_side_conflict", className(action.getModelAction()));
	}

	@Override
	public void onUserLogin(final User user) {
		GoogleAnalyticsNativeImpl.setCustomDimension(DIMENSION_USER, user.getId().toString());
	}

	@Override
	public void onUserLogout() {
		GoogleAnalyticsNativeImpl.setCustomDimension(DIMENSION_USER, ANONYMOUS_USER);
	}

	@Override
	public void onProjectChange(final UUID projectId) {
		GoogleAnalyticsNativeImpl.setCustomDimension(DIMENSION_PROJECT, projectId == null ? NO_PROJECT : projectId.toString());
	}

	@Override
	public void onClientClose(final int nOfPendingActions) {
		GoogleAnalyticsNativeImpl.sendEvent("application", "close", nOfPendingActions == 0 ? "ok" : "has_pending_actions", nOfPendingActions);
	}

	@Override
	public void onPendingActionsSavedLocally(final int savedActionsCount) {
		GoogleAnalyticsNativeImpl.sendEvent("pending_actions", "save", null, savedActionsCount);
	}

	@Override
	public void onLocallySavedPendingActionsLoaded(final int savedActionsCount) {
		GoogleAnalyticsNativeImpl.sendEvent("pending_actions", "load", null, savedActionsCount);
	}

	@Override
	public void onLocallySavedPendingActionsSync(final boolean success, final int pendingActionsCount) {
		GoogleAnalyticsNativeImpl.sendEvent("pending_actions", "sync", success ? "success" : "failed", pendingActionsCount);
	}

	@Override
	public void onConnectionLost() {
		GoogleAnalyticsNativeImpl.sendEvent("application", "connection_lost");
	}

	@Override
	public void onConnectionRecovered() {
		GoogleAnalyticsNativeImpl.sendEvent("application", "connection_recovered");
	}

	@Override
	public void onShortcutUsed(final ShortcutMapping<?> mapping) {
		GoogleAnalyticsNativeImpl.sendEvent("application", "shortcut_used", shortcutName(mapping));
	}

	private String shortcutName(final ShortcutMapping<?> mapping) {
		return className(mapping) + "." + mapping.name();
	}

	private String className(final Object placeType) {
		return MetricsTokenizer.getClassSimpleName(placeType);
	}

}
