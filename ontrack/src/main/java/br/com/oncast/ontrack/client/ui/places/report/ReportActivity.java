package br.com.oncast.ontrack.client.ui.places.report;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ReportActivity extends AbstractActivity {

	private static final ClientServiceProvider PROVIDER = ClientServiceProvider.getInstance();
	private final ReportPlace place;

	private final UUID requestedProjectId;

	public ReportActivity(final ReportPlace place) {
		this.place = place;
		this.requestedProjectId = place.getRequestedProjectId();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ReportPanel view = new ReportPanel();

		final ProjectContext projectContext = ClientServiceProvider.getCurrentProjectContext();

		final ApplicationMenu menu = view.getApplicationMenu();
		menu.clearCustomMenuItems();
		menu.setBackButtonVisibility(true);

		PROVIDER.getClientAlertingService().setAlertingParentWidget(view.getAlertingContainer());
		panel.setWidget(view);
	}

	@Override
	public void onStop() {}
}
