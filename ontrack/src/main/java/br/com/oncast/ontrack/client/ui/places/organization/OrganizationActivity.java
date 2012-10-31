package br.com.oncast.ontrack.client.ui.places.organization;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.organization.OrganizationPanel;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootPanel;

public class OrganizationActivity extends AbstractActivity {

	private HandlerRegistration registration;

	public OrganizationActivity(final OrganizationPlace place) {}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final OrganizationPanel view = new OrganizationPanel();
		panel.setWidget(view);

		final ApplicationMenu menu = view.getApplicationMenu();
		menu.clearCustomMenuItems();
		menu.setBackButtonVisibility(true);

		registration = ShortcutService.register(RootPanel.get(), ClientServiceProvider.getInstance().getActionExecutionService(),
				UndoRedoShortCutMapping.values());

		ClientServiceProvider.getInstance().getClientAlertingService().setAlertingParentWidget(view.getAlertingContainer());
	}

	@Override
	public void onStop() {
		registration.removeHandler();
	}

}
