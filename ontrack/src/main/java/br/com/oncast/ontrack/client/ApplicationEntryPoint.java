package br.com.oncast.ontrack.client;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ApplicationEntryPoint implements EntryPoint {

	private static final PlannnigPlace DEFAULT_APP_PLACE = new PlannnigPlace("");

	@Override
	public void onModuleLoad() {
		final SimplePanel rootPanel = new SimplePanel();
		rootPanel.setStyleName("rootPanel");
		RootPanel.get().add(rootPanel);

		// FIXME Configure a action execution listener that dispatches executed actions, for that should create an ActionSyncService
		// TODO Configure a communication channel with a listener for server sent actions
		// TODO Configure communication error handlers
		final ClientServiceProvider clientServiceProvider = new ClientServiceProvider();
		clientServiceProvider.getApplicationPlaceController().configure(rootPanel, DEFAULT_APP_PLACE, new AppActivityMapper(clientServiceProvider),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}
}

// System.out.println("Action sent: " + action.getScopeId() + " " + action.getClass());
// transmissionService.transmitAction(action, new AsyncCallback<Void>() {
//
// @Override
// public void onSuccess(final Void result) {
// System.out.println("Success!");
// }
//
// @Override
// public void onFailure(final Throwable caught) {
// System.out.println("Failure!");
// }
// });