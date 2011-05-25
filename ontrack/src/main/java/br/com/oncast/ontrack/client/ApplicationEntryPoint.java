package br.com.oncast.ontrack.client;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.place.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.place.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.client.ui.place.scope.ScopePlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ApplicationEntryPoint implements EntryPoint {

	private static final ScopePlace DEFAULT_APP_PLACE = new ScopePlace("");

	@Override
	public void onModuleLoad() {
		final SimplePanel rootPanel = new SimplePanel();
		rootPanel.setStyleName("rootPanel");
		RootPanel.get().add(rootPanel);

		ClientServiceProvider.getInstance().getApplicationPlaceController()
				.configure(rootPanel, DEFAULT_APP_PLACE, new AppActivityMapper(), (PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}
}
