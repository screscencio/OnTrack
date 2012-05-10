package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.loading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.loading.UserInformationLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressActivity;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationActivity;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationPlace;
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

	@Override
	public Activity getActivity(final Place place) {

		if (place instanceof LoginPlace) return createLoginActivity((LoginPlace) place);

		if (!services.getAuthenticationService().isUserAvailable()) return createUserInformationLoadingActivity(place);

		if (place instanceof ProjectDependentPlace) {
			final ProjectDependentPlace projectDependentPlace = (ProjectDependentPlace) place;
			final long requestedProjectId = projectDependentPlace.getRequestedProjectId();

			if (requestedProjectId <= 0) return createProjectSelectionActivity();
			if (!services.getContextProviderService().isContextAvailable(requestedProjectId)) return createContextLoadingActivity(projectDependentPlace);
		}

		if (place instanceof PlanningPlace) return createPlanningActivity((PlanningPlace) place);
		if (place instanceof ProjectSelectionPlace) return createProjectSelectionActivity();
		if (place instanceof ProjectCreationPlace) return createProjectCreationPlace((ProjectCreationPlace) place);
		if (place instanceof ProgressPlace) return createProgressActivity((ProgressPlace) place);

		return null;
	}

	private Activity createProgressActivity(final ProgressPlace place) {
		return new ProgressActivity(place);
	}

	private Activity createProjectCreationPlace(final ProjectCreationPlace place) {
		return new ProjectCreationActivity(place);
	}

	private Activity createProjectSelectionActivity() {
		return new ProjectSelectionActivity();
	}

	private Activity createLoginActivity(final LoginPlace loginPlace) {
		return new LoginActivity(loginPlace.getDestinationPlace());
	}

	private PlanningActivity createPlanningActivity(final ProjectDependentPlace place) {
		return new PlanningActivity();
	}

	private ContextLoadingActivity createContextLoadingActivity(final ProjectDependentPlace projectDependentPlace) {
		return new ContextLoadingActivity(projectDependentPlace);
	}

	private UserInformationLoadingActivity createUserInformationLoadingActivity(final Place place) {
		return new UserInformationLoadingActivity(place);
	}
}
