package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private final ClientServiceProvider services;

	public AppActivityMapper(final ClientServiceProvider serviceProvider) {
		this.services = serviceProvider;
	}

	// TODO Potentially lazy load and store activity instances. (ContextLoadingPlace should have the destination place set or should always have a new instance)
	@Override
	public Activity getActivity(final Place place) {
		if (place instanceof ContextLoadingPlace) return new ContextLoadingActivity(services.getContextProviderService(),
				services.getApplicationPlaceController(), services.getCommunicationService(), (ContextLoadingPlace) place);
		if (place instanceof PlannnigPlace) return new PlanningActivity(services.getActionExecutionService(), services.getContextProviderService());

		return null;
	}

}
