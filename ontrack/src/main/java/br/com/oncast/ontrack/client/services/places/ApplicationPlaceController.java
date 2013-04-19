package br.com.oncast.ontrack.client.services.places;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.places.AppActivityManager;
import br.com.oncast.ontrack.client.ui.places.RestorablePlace;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

public class ApplicationPlaceController {

	private static final String NEW_WINDOW_EVERY_TIME = "_blank";
	private static final String FEATURES = "chrome=yes,width=900,outerHeight=800";
	private final PlaceController placeController;
	private final EventBus eventBus;
	private boolean configured;
	private final Set<PlaceChangeListener> placeChangeListeners;
	private PlaceHistoryMapper placeHistoryMapper;
	private ClientStorageService clientStorageService;
	private ClientMetricsService metricsService;

	public ApplicationPlaceController(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.placeChangeListeners = new HashSet<PlaceChangeListener>();
		placeController = new PlaceController(eventBus);
	}

	public void open(final OpenInNewWindowPlace place) {
		metricsService.onNewWindowPlaceRequest(place);
		final OpenInNewWindowPlace p = place;
		String url = GWT.getHostPageBaseURL() + "#";
		url += p.getPlacePrefix() + ":";
		url += p.getToken();
		Window.open(url, NEW_WINDOW_EVERY_TIME, FEATURES);
	}

	public void goTo(final Place place) {
		if (place instanceof RestorablePlace) clientStorageService.storeDefaultPlaceToken(placeHistoryMapper.getToken(place));
		metricsService.onPlaceRequest(place);
		placeController.goTo(place);
	}

	public void configure(final AcceptsOneWidget container, final Place defaultAppPlace, final ActivityMapper activityMapper,
			final PlaceHistoryMapper placeHistoryMapper, final ClientStorageService clientStorageService, final ClientMetricsService metricsService) {

		this.placeHistoryMapper = placeHistoryMapper;
		this.clientStorageService = clientStorageService;
		this.metricsService = metricsService;
		if (configured) throw new RuntimeException("The placeController is already configured.");
		configured = true;

		final AppActivityManager activityManager = new AppActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(container);

		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(placeHistoryMapper);
		final String defaultPlaceToken = clientStorageService.loadDefaultPlaceToken();
		final Place place = defaultPlaceToken == null ? defaultAppPlace : placeHistoryMapper.getPlace(defaultPlaceToken);
		historyHandler.register(placeController, eventBus, place == null ? defaultAppPlace : place);
		historyHandler.handleCurrentHistory();

		eventBus.addHandler(PlaceChangeRequestEvent.TYPE, new PlaceChangeRequestEvent.Handler() {

			@Override
			public void onPlaceChangeRequest(final PlaceChangeRequestEvent event) {
				notifyPlaceChangeListeners(event.getNewPlace());
			}
		});
	}

	public Place getCurrentPlace() {
		return placeController.getWhere();
	}

	public void addPlaceChangeListener(final PlaceChangeListener placeChangeListener) {
		placeChangeListeners.add(placeChangeListener);
	}

	protected void notifyPlaceChangeListeners(final Place newPlace) {
		for (final PlaceChangeListener listener : placeChangeListeners) {
			listener.onPlaceChange(newPlace);
		}
	}

}
