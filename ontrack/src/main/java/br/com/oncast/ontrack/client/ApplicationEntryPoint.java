package br.com.oncast.ontrack.client;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.ui.ApplicationUIPanel;
import br.com.oncast.ontrack.client.ui.nativeeventhandlers.BrowserEscapeKeyDefaultActionPreventer;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationEntryPoint implements EntryPoint {

	public static final Place DEFAULT_APP_PLACE = new ProjectSelectionPlace();

	@Override
	public void onModuleLoad() {
		ignoreBrowserDefaultActionForEscapeKey();
		setUpClientServices();
	}

	/**
	 * Ignores the default browser action for the ESC key down event, that in some cases closes all current HTTP requests (AJAX requests as well).
	 */
	private void ignoreBrowserDefaultActionForEscapeKey() {
		GlobalNativeEventService.getInstance().addKeyDownListener(new BrowserEscapeKeyDefaultActionPreventer());
	}

	private void setUpClientServices() {
		final ApplicationUIPanel applicationUIPanel = new ApplicationUIPanel();
		RootPanel.get().add(applicationUIPanel);

		ClientServiceProvider.getInstance().configure(applicationUIPanel, DEFAULT_APP_PLACE);
	}
}