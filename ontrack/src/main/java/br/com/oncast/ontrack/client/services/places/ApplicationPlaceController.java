package br.com.oncast.ontrack.client.services.places;

import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ApplicationPlaceController {

	private final PlaceController placeController;
	private final EventBus eventBus;
	private boolean configured;

	public ApplicationPlaceController(final EventBus eventBus) {
		this.eventBus = eventBus;
		placeController = new PlaceController(eventBus);
	}

	public void goTo(final Place place) {
		placeController.goTo(place);
	}

	public void configure(final AcceptsOneWidget container, final PlannnigPlace defaultAppPlace, final ActivityMapper activityMapper,
			final PlaceHistoryMapper placeHistoryMapper) {
		if (configured) throw new RuntimeException("The placeController is already configured.");
		configured = true;

		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		final AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		activityManager.setDisplay(container);
		historyHandler.register(placeController, eventBus, defaultAppPlace);
		historyHandler.handleCurrentHistory();
	}
}
