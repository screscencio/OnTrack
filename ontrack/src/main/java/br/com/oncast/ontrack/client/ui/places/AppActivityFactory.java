package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.ui.places.details.DetailActivity;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.client.ui.places.loading.ContextLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.loading.UserInformationLoadingActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginActivity;
import br.com.oncast.ontrack.client.ui.places.login.LoginPlace;
import br.com.oncast.ontrack.client.ui.places.metrics.OnTrackMetricsActivity;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationActivity;
import br.com.oncast.ontrack.client.ui.places.organization.OrganizationPlace;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressActivity;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationActivity;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionActivity;
import br.com.oncast.ontrack.client.ui.places.report.ReportActivity;
import br.com.oncast.ontrack.client.ui.places.report.ReportPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

public class AppActivityFactory {

	protected Activity getOrganizationActivity(final OrganizationPlace place) {
		return new OrganizationActivity(place);
	}

	protected Activity getConnectServerPushLoadingActivity(final Place place) {
		return new ServerPushLoadingActivity(place);
	}

	protected DetailActivity getDetailActivity(final DetailPlace place) {
		return new DetailActivity(place);
	}

	protected Activity getProgressActivity(final ProgressPlace place) {
		return new ProgressActivity(place);
	}

	protected Activity getProjectCreationPlace(final ProjectCreationPlace place) {
		return new ProjectCreationActivity(place);
	}

	protected Activity getProjectSelectionActivity() {
		return new ProjectSelectionActivity();
	}

	protected Activity getLoginActivity(final LoginPlace loginPlace) {
		return new LoginActivity(loginPlace.getDestinationPlace());
	}

	protected PlanningActivity getPlanningActivity(final PlanningPlace place) {
		return new PlanningActivity(place);
	}

	protected ContextLoadingActivity getContextLoadingActivity(final ProjectDependentPlace projectDependentPlace) {
		return new ContextLoadingActivity(projectDependentPlace);
	}

	protected UserInformationLoadingActivity getUserInformationLoadingActivity(final Place place) {
		return new UserInformationLoadingActivity(place);
	}

	public Activity getOnTrackStatisticsActivity() {
		return new OnTrackMetricsActivity();
	}

	public Activity getReportActivity(final ReportPlace place) {
		return new ReportActivity(place);
	}
}
