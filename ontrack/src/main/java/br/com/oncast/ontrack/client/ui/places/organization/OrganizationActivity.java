package br.com.oncast.ontrack.client.ui.places.organization;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.OrganizationPanel;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class OrganizationActivity extends AbstractActivity {

	private static final ClientServices PROVIDER = ClientServices.get();
	private final OrganizationPlace place;

	public OrganizationActivity(final OrganizationPlace place) {
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final TimeTrackingEvent timeTracking = ClientServices.get().metrics().startPlaceLoad(place);
		Window.setTitle(DefaultViewSettings.TITLE);
		final OrganizationPanel view = new OrganizationPanel(place.getProject());
		panel.setWidget(view);

		final ApplicationMenu menu = view.getApplicationMenu();
		menu.clearCustomMenuItems();
		menu.setBackButtonVisibility(true);

		PROVIDER.alerting().setAlertingParentWidget(view.getAlertingContainer());
		timeTracking.end();
	}

	@Override
	public void onStop() {}

}
