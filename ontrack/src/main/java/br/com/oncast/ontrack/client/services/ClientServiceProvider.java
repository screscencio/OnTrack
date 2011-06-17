package br.com.oncast.ontrack.client.services;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.communication.CommunicationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

// TODO Create interfaces for each service and return them instead of the direct reference of its implementations (so that the rest of the application only
// reference the interfaces, making the code more testable).
public class ClientServiceProvider {

	// TODO Review: This service is instantiated at construction so it is initialized as soon as possible.
	private final ActionSyncService actionSyncService;
	private ApplicationPlaceController placeController;
	private CommunicationService communicationService;
	private ContextProviderService contextProviderService;
	private ActionExecutionService actionExecutionService;
	private EventBus eventBus;

	public ClientServiceProvider() {
		actionSyncService = new ActionSyncService(getCommunicationService(), getActionExecutionService());
	}

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
		return contextProviderService = new ContextProviderServiceImpl();
	}

	public CommunicationService getCommunicationService() {
		if (communicationService != null) return communicationService;
		return communicationService = new CommunicationService();
	}

	public ActionSyncService getActionSyncService() {
		return actionSyncService;
	}

	private EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
