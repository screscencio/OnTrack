package br.com.oncast.ontrack.client.service;

import br.com.oncast.ontrack.client.service.place.AppPlaceController;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class ClientServiceProvider {

	private static ClientServiceProvider instance;
	private AppPlaceController placeController;
	private EventBus eventBus;

	private ClientServiceProvider() {}

	public static ClientServiceProvider getInstance() {
		if (instance != null) return instance;
		return instance = new ClientServiceProvider();
	}

	public AppPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new AppPlaceController();
	}

	public EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
