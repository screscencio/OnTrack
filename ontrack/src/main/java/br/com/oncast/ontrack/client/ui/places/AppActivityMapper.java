package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingPlace;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
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

	// TODO +++++Potentially lazy load and store activity instances. (ContextLoadingActivity/LoginActivity should have the destination place set or should
	// always
	// have a new instance)
	@Override
	public Activity getActivity(final Place place) {
		// XXX Auth; Lazy load login activity when login place is received.
		if (!services.getAuthenticationService().isUserLoggedIn()) return createLoginActivity(place);

		if (place instanceof ContextLoadingPlace) return createContextLoadingActivity(((ContextLoadingPlace) place).getDestinationPlace());
		if (!services.getContextProviderService().isContextAvailable()) return createContextLoadingActivity(place);

		if (place instanceof PlannnigPlace) return createPlanningActivity();

		return null;
	}

	private Activity createLoginActivity(final Place place) {
		return new LoginActivity(services.getAuthenticationService(), services.getApplicationPlaceController(), place);
	}

	private PlanningActivity createPlanningActivity() {
		return new PlanningActivity(services.getActionExecutionService(), services.getContextProviderService(), services.getAuthenticationService());
	}

	private ContextLoadingActivity createContextLoadingActivity(final Place place) {
		return new ContextLoadingActivity(services.getContextProviderService(), services.getApplicationPlaceController(), services.getRequestDispatchService(),
				place);
	}
}
