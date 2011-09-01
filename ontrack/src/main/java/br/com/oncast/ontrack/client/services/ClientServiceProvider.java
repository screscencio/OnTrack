package br.com.oncast.ontrack.client.services;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchServiceImpl;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

// TODO Create interfaces for each service and return them instead of the direct reference of its implementations (so that the rest of the application only
// reference the interfaces, making the code more testable).
public class ClientServiceProvider {

	private ActionSyncService actionSyncService;
	private ActionExecutionService actionExecutionService;
	private ContextProviderService contextProviderService;
	private RequestDispatchService requestDispatchService;
	private ServerPushClientService serverPushClientService;
	private ClientIdentificationProvider clientIdentificationProvider;
	private ApplicationPlaceController placeController;
	private EventBus eventBus;

	public ApplicationPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(getEventBus());
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(getContextProviderService());
	}

	public ContextProviderService getContextProviderService() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderServiceImpl();
	}

	public RequestDispatchService getRequestDispatchService() {
		if (requestDispatchService != null) return requestDispatchService;
		return requestDispatchService = new RequestDispatchServiceImpl();
	}

	public ActionSyncService getActionSyncService() {
		if (actionSyncService != null) return actionSyncService;
		return actionSyncService = new ActionSyncService(getRequestDispatchService(), getServerPushClientService(), getActionExecutionService(),
				getClientIdentificationProvider());
	}

	private ClientIdentificationProvider getClientIdentificationProvider() {
		if (clientIdentificationProvider != null) return clientIdentificationProvider;
		return clientIdentificationProvider = new ClientIdentificationProvider();
	}

	private ServerPushClientService getServerPushClientService() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl();
	}

	private EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
