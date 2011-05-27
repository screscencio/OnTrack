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

		ClientServiceProvider.getInstance().getApplicationPlaceController()
				.configure(rootPanel, DEFAULT_APP_PLACE, new AppActivityMapper(), (PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}
}
