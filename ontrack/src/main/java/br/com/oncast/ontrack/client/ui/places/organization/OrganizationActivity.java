package br.com.oncast.ontrack.client.ui.places.organization;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.OrganizationPanel;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class OrganizationActivity extends AbstractActivity {

	private static final ClientServiceProvider PROVIDER = ClientServiceProvider.getInstance();
	private final OrganizationPlace place;

	public OrganizationActivity(final OrganizationPlace place) {
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		Window.setTitle(DefaultViewSettings.TITLE);
		final OrganizationPanel view = new OrganizationPanel(place.getProject());
		panel.setWidget(view);

		final ApplicationMenu menu = view.getApplicationMenu();
		menu.clearCustomMenuItems();
		menu.setBackButtonVisibility(true);

		PROVIDER.getClientAlertingService().setAlertingParentWidget(view.getAlertingContainer());
	}

	@Override
	public void onStop() {}

}
