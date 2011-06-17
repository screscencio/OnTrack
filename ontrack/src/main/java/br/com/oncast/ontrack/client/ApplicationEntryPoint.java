package br.com.oncast.ontrack.client;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.ApplicationUIPanel;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.client.ui.places.planning.PlannnigPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationEntryPoint implements EntryPoint {

	private static final Place DEFAULT_APP_PLACE = new PlannnigPlace("");

	@Override
	public void onModuleLoad() {
		final ApplicationUIPanel applicationUIPanel = new ApplicationUIPanel();
		RootPanel.get().add(applicationUIPanel);

		// TODO Configure communication error handlers
		final ClientServiceProvider clientServiceProvider = new ClientServiceProvider();
		clientServiceProvider.getApplicationPlaceController().configure(applicationUIPanel, DEFAULT_APP_PLACE, new AppActivityMapper(clientServiceProvider),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}
}