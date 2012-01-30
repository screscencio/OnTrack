package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.ReleaseSelectionWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.ProgressPanelActionSyncController;
import br.com.oncast.ontrack.client.ui.components.progresspanel.ProgressPanelActionSyncController.Display;
import br.com.oncast.ontrack.client.ui.generalwidgets.BreadcrumbWidget;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class ProgressActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private ProgressPanelActionSyncController progressPanelActionSyncController;
	private ProgressView view;
	private ProjectContext projectContext;
	private Release release;

	public ProgressActivity(final ProgressPlace place) {
		try {
			projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(place.getRequestedProjectId());
			release = projectContext.findRelease(place.getRequestedReleaseId());
			view = new ProgressPanel();

			progressPanelActionSyncController = new ProgressPanelActionSyncController(SERVICE_PROVIDER.getActionExecutionService(), release, new Display() {

				@Override
				public void update() {
					updateViewData();
				}

				@Override
				public void exit() {
					exitToPlanningPlace();
				}

				@Override
				public void updateReleaseInfo() {
					updateViewMenus();
				}
			});
		}
		catch (final ReleaseNotFoundException e) {
			exitToPlanningPlace();
		}
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		if (view == null) throw new RuntimeException("The view wasnt initialized correctly.");

		updateViewData();
		updateViewMenus();

		panel.setWidget(view);

		progressPanelActionSyncController.registerActionExecutionListener();
	}

	@Override
	public void onStop() {
		progressPanelActionSyncController.unregisterActionExecutionListener();
	}

	protected void updateViewData() {
		view.getKanbanPanel().configureKanbanPanel(projectContext.getKanban(release), release);
	}

	private void updateViewMenus() {
		view.getApplicationMenu().setCustomItem(createBreadcrumb());
	}

	private void exitToPlanningPlace() {
		final long projectId = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrentProjectRepresentation().getId();
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(new PlanningPlace(projectId));
	}

	private Widget createBreadcrumb() {
		final BreadcrumbWidget breadcrumb = new BreadcrumbWidget();
		final ProjectRepresentation project = projectContext.getProjectRepresentation();
		breadcrumb.addItem(project.getName(), new Command() {
			@Override
			public void execute() {
				exitToPlanningPlace();
			}
		});
		breadcrumb.addSeparator();
		breadcrumb.addPopupItem(release.getFullDescription(), new ReleaseSelectionWidget());
		return breadcrumb;
	}
}