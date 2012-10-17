package br.com.oncast.ontrack.client.services.places;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.storage.ClientStorageService;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

public class ApplicationPlaceController {

	private final PlaceController placeController;
	private final EventBus eventBus;
	private boolean configured;
	private final Set<PlaceChangeListener> placeChangeListeners;
	private PlaceHistoryMapper placeHistoryMapper;
	private ClientStorageService clientStorageService;

	public ApplicationPlaceController(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.placeChangeListeners = new HashSet<PlaceChangeListener>();
		placeController = new PlaceController(eventBus);
	}

	public void goTo(final Place place) {
		clientStorageService.storeDefaultPlaceToken(placeHistoryMapper.getToken(place));
		placeController.goTo(place);
	}

	public void configure(final AcceptsOneWidget container, final Place defaultAppPlace, final ActivityMapper activityMapper,
			final PlaceHistoryMapper placeHistoryMapper, final ClientStorageService clientStorageService) {

		this.placeHistoryMapper = placeHistoryMapper;
		this.clientStorageService = clientStorageService;
		if (configured) throw new RuntimeException("The placeController is already configured.");
		configured = true;

		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(placeHistoryMapper);

		activityManager.setDisplay(container);
		final String defaultPlaceToken = clientStorageService.loadDefaultPlaceToken();
		final Place place = defaultPlaceToken == null ? defaultAppPlace : placeHistoryMapper.getPlace(defaultPlaceToken);
		historyHandler.register(placeController, eventBus, place);
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
