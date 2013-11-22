package br.com.oncast.ontrack.client.services.metrics;

import br.com.oncast.ontrack.client.services.places.OpenInNewWindowPlace;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;

import java.util.Date;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ClientMetricsService {

	void getRealTimeMetrics(Date lastMetricsUpdate, AsyncCallback<OnTrackRealTimeServerMetrics> callback);

	void onPlaceRequest(Place place);

	TimeTrackingEvent startPlaceLoad(Place place);

	TimeTrackingEvent startPlaceLoad(Class<? extends Place> placeType);

	TimeTrackingEvent startTimeTracking(MetricsCategories category, String eventName);

	void onException(String message);

	void onNewWindowPlaceRequest(OpenInNewWindowPlace place);

	void getServerStatistics(AsyncCallback<OnTrackServerStatistics> asyncCallback);

	void onUserLogin(User user);

	void onUserLogout();

	void onClientClose(int nOfPendingActions);

	void onPendingActionsSavedLocally(int savedActionsCount);

	void onLocallySavedPendingActionsLoaded(int savedActionsCount);

	void onLocallySavedPendingActionsSync(boolean success, int pendingActionsCount);

	void onConnectionLost();

	void onConnectionRecovered();

	void onActionExecution(ModelAction action, boolean isClientOnline);

	void onShortcutUsed(ShortcutMapping<?> mapping);

	void onProjectChange(UUID projectId);

}
