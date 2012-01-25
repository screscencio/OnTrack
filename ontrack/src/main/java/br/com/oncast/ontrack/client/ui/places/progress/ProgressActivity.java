package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ProgressActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private final ProgressPlace place;
	private ProgressView view;

	public ProgressActivity(final ProgressPlace place) {
		this.place = place;
		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	// FIXME Lobo: Show the Project and release somewhere
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		view = new ProgressPanel();

		final long projectId = place.getRequestedProjectId();
		final UUID releaseId = place.getRequestedReleaseId();

		try {
			final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(projectId);
			view.getKanbanPanel().setKanban(projectContext.getKanban(projectContext.findRelease(releaseId)));
		}
		catch (final ReleaseNotFoundException e) {
			// FIXME LOBO
			SERVICE_PROVIDER.getApplicationPlaceController().goTo(new PlanningPlace(projectId));
		}
		// FIXME LOBO Change the runtime exception
		catch (final RuntimeException e) {
			SERVICE_PROVIDER.getApplicationPlaceController().goTo(new ProjectSelectionPlace());
		}

		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
	}
}