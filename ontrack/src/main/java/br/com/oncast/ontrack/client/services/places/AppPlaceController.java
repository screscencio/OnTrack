package br.com.oncast.ontrack.client.services.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.place.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.client.ui.place.planning.PlannnigPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class AppPlaceController {

	private final PlaceController placeController;
	private boolean configured;

	public AppPlaceController() {
		placeController = new PlaceController(ClientServiceProvider.getInstance().getEventBus());
	}

	public void goTo(final Place place) {
		placeController.goTo(place);
	}

	public void configure(final AcceptsOneWidget container, final PlannnigPlace defaultAppPlace, final ActivityMapper activityMapper,
			final PlaceHistoryMapper placeHistoryMapper) {
		if (configured) throw new RuntimeException("The placeController is already configured.");
		configured = true;

		final EventBus eventBus = ClientServiceProvider.getInstance().getEventBus();
		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		final AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		activityManager.setDisplay(container);
		historyHandler.register(placeController, eventBus, defaultAppPlace);
		historyHandler.handleCurrentHistory();
	}
}
