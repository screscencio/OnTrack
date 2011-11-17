package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

	private final ClientServiceProvider services;

	public AppActivityMapper(final ClientServiceProvider serviceProvider) {
		this.services = serviceProvider;
	}

	// TODO +++++Potentially lazy load and store activity instances. (ContextLoadingActivity/LoginActivity should have
	// the destination place set or should always have a new instance)
	@Override
	public Activity getActivity(final Place place) {
		// XXX Auth; Lazy load login activity when login place is received.
		if (!services.getAuthenticationService().isUserLoggedIn()) return createLoginActivity(place);

		if (place instanceof ProjectDependentPlace) {
			final ProjectDependentPlace projectDependentPlace = (ProjectDependentPlace) place;
			final long requestedProjectId = projectDependentPlace.getRequestedProjectId();

			// FIXME To be implemented in this story.
			// if (requestedProjectId == 0) return createProjectSelectionActivity();
			if (!services.getContextProviderService().isContextAvailable(requestedProjectId)) return createContextLoadingActivity(projectDependentPlace);
		}

		if (place instanceof PlanningPlace) return createPlanningActivity((PlanningPlace) place);

		return null;
	}

	private Activity createLoginActivity(final Place place) {
		return new LoginActivity(services.getAuthenticationService(), services.getApplicationPlaceController(), place);
	}

	private PlanningActivity createPlanningActivity(final ProjectDependentPlace place) {
		return new PlanningActivity(services.getActionExecutionService(), services.getContextProviderService(), services.getAuthenticationService(),
				services.getProjectRepresentationProvider());
	}

	private ContextLoadingActivity createContextLoadingActivity(final ProjectDependentPlace projectDependentPlace) {
		return new ContextLoadingActivity(services.getContextProviderService(), services.getApplicationPlaceController(), services.getRequestDispatchService(),
				services.getProjectRepresentationProvider(),
				projectDependentPlace);
	}
}
