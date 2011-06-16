package br.com.oncast.ontrack.client.services;

import br.com.oncast.ontrack.client.services.actions.ActionExecutionService;
import br.com.oncast.ontrack.client.services.communication.CommunicationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

// FIXME Create interfaces for each service and return them instead of the direct reference of its implementations.
public class ClientServiceProvider {

	private ApplicationPlaceController placeController;
	private CommunicationService communicationService;
	private ContextProviderService contextProviderService;
	private ActionExecutionService actionExecutionService;
	private EventBus eventBus;

	public ClientServiceProvider() {}

	public ApplicationPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(getEventBus());
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionService(getContextProviderService());
	}

	public ContextProviderService getContextProviderService() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderService();
	}

	public CommunicationService getCommunicationService() {
		if (communicationService != null) return communicationService;
		return communicationService = new CommunicationService();
	}

	private EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
