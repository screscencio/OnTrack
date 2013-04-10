package br.com.oncast.ontrack.client.ui.places.projectSelection;

import br.com.oncast.ontrack.client.services.ClientServices;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

// TODO Add logout button to this activity.
public class ProjectSelectionActivity extends AbstractActivity {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	public ProjectSelectionActivity() {}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ProjectSelectionView view = new ProjectSelectionPanel();
		panel.setWidget(view.asWidget());
		view.focus();
		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.asWidget());

	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
	}
}
