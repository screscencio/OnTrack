package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.contextloading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionActivity;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

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
		// FIXME Test this

		if (place instanceof LoginPlace) return createLoginActivity(((LoginPlace) place).getDestinationPlace());
		// XXX Auth; Lazy load login activity when login place is received. Remove this.
		if (!services.getAuthenticationService().isUserLoggedIn()) return createLoginActivity(place);

		if (place instanceof ProjectDependentPlace) {
			final ProjectDependentPlace projectDependentPlace = (ProjectDependentPlace) place;
			final long requestedProjectId = projectDependentPlace.getRequestedProjectId();

			if (requestedProjectId == 0) return createProjectSelectionActivity();
			if (!services.getContextProviderService().isContextAvailable(requestedProjectId)) return createContextLoadingActivity(projectDependentPlace);
		}

		if (place instanceof PlanningPlace) return createPlanningActivity((PlanningPlace) place);
		if (place instanceof ProjectSelectionPlace) return createProjectSelectionActivity();

		return null;
	}

	private Activity createProjectSelectionActivity() {
		return new ProjectSelectionActivity();
	}

	// TODO Receive a LoginPlace
	private Activity createLoginActivity(final Place destinationPlace) {
		return new LoginActivity(destinationPlace);
	}

	private PlanningActivity createPlanningActivity(final ProjectDependentPlace place) {
		return new PlanningActivity();
	}

	private ContextLoadingActivity createContextLoadingActivity(final ProjectDependentPlace projectDependentPlace) {
		return new ContextLoadingActivity(projectDependentPlace);
	}
}
