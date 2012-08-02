package br.com.oncast.ontrack.client.services.places;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
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

	public ApplicationPlaceController(final EventBus eventBus) {
		this.eventBus = eventBus;
		this.placeChangeListeners = new HashSet<PlaceChangeListener>();
		placeController = new PlaceController(eventBus);
	}

	public void goTo(final Place place) {
		placeController.goTo(place);
	}

	public void configure(final AcceptsOneWidget container, final Place defaultAppPlace, final ActivityMapper activityMapper,
			final PlaceHistoryMapper placeHistoryMapper) {

		if (configured) throw new RuntimeException("The placeController is already configured.");
		configured = true;

		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		final AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		activityManager.setDisplay(container);
		historyHandler.register(placeController, eventBus, defaultAppPlace);
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
