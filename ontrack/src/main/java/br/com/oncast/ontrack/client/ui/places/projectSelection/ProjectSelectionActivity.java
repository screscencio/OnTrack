package br.com.oncast.ontrack.client.ui.places.projectSelection;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

// TODO Add logout button to this activity.
public class ProjectSelectionActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	public ProjectSelectionActivity() {
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ProjectSelectionView view = new ProjectSelectionPanel();
		panel.setWidget(view.asWidget());
		view.focus();
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.asWidget());

		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadEnd();
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientAlertingService().clearAlertingParentWidget();
	}
}
