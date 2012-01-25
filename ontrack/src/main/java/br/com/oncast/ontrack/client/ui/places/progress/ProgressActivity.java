package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ProgressActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private ProgressView view;
	private final Release release;
	private final String projectName;

	public ProgressActivity(final ProgressPlace place) {
		activityActionExecutionListener = new ActivityActionExecutionListener();
		final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(place.getRequestedProjectId());
		release = projectContext.getProjectRelease().findRelease(place.getRequestedReleaseId());
		projectName = projectContext.getProjectRepresentation().getName();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		Window.setTitle(projectName + " - " + release.getDescription());
		view = new ProgressPanel();

		for (final Scope scope : release.getAllScopesIncludingChildrenReleases()) {
			final Progress prog = scope.getProgress();
			view.addItem(prog, release.getScopeIndex(scope), scope);
		}

		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		Window.setTitle(DefaultViewSettings.TITLE);

		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
	}

}