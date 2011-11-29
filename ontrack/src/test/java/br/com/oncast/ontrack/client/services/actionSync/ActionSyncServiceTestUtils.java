package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.BusinessLogicMockFactoryTestUtils;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectCreatedEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ActionSyncServiceTestUtils {

	private static final int DEFAULT_PROJECT_ID = 1;

	protected interface ProjectContextLoadCallback {
		void onProjectContextLoaded(ProjectContext context);

		void onProjectContextFailed(Throwable caught);
	}

	protected class ValueHolder<T> {
		T value;

		public ValueHolder(final T initialValue) {
			value = initialValue;
		}

		public T getValue() {
			return value;
		}

		public void setValue(final T value) {
			this.value = value;
		}
	}

	protected final class ServerPushClientServiceMockImpl implements ServerPushClientService {
		private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();

		@Override
		public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass, final ServerPushEventHandler<T> serverPushEventHandler) {
			if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

			final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
			handlerList.add(serverPushEventHandler);
		}

		public void processIncommingEvent(final ServerPushEvent event) {
			for (final ServerPushEventHandler<?> handler : eventHandlersMap.get(event.getClass())) {
				notifyEventHandler(handler, event);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void notifyEventHandler(final ServerPushEventHandler handler, final ServerPushEvent event) {
			handler.onEvent(event);
		}
	}

	private static final ClientIdentificationProvider CLIENT_IDENTIFICATION_PROVIDER = new ClientIdentificationProvider();
	private RequestDispatchService requestDispatchService;
	private ServerPushClientServiceMockImpl serverPushClientService;
	private ActionExecutionService actionExecutionService;
	private BusinessLogic businessLogic;
	private MulticastService multicastService;
	private ErrorTreatmentService errorTreatmentService;

	public ActionExecutionService getActionExecutionServiceMock() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionService() {

			private final ProjectContext projectContext = ProjectTestUtils.createProjectContext(new Scope("root", new UUID("0")), new Release("proj", new UUID(
					"r0")));
			private final List<ActionExecutionListener> actionExecutionListeners = new ArrayList<ActionExecutionListener>();

			@Override
			public void onUserActionExecutionRequest(final ModelAction action) {
				notifyActionExecutionListeners(action, projectContext, new HashSet<UUID>(), true);
			}

			@Override
			public void onNonUserActionRequest(final ModelAction action) throws UnableToCompleteActionException {
				notifyActionExecutionListeners(action, projectContext, new HashSet<UUID>(), false);
			}

			@Override
			public void onUserActionUndoRequest() {
				throw new RuntimeException();
			}

			@Override
			public void onUserActionRedoRequest() {
				throw new RuntimeException();
			}

			@Override
			public void addActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
				if (this.actionExecutionListeners.contains(actionExecutionListener)) return;
				this.actionExecutionListeners.add(actionExecutionListener);
			}

			@Override
			public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
				this.actionExecutionListeners.remove(actionExecutionListener);
			}

			private void notifyActionExecutionListeners(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				for (final ActionExecutionListener handler : actionExecutionListeners) {
					handler.onActionExecution(action, context, inferenceInfluencedScopeSet, isUserAction);
				}
			}
		};
	}

	public ServerPushClientServiceMockImpl getServerPushClientServiceMock() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceMockImpl();
	}

	public BusinessLogic getBusinessLogicMock() {
		if (businessLogic != null) return businessLogic;
		return businessLogic = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndCustomBroadcastMock(getMulticastServiceMock());
	}

	public MulticastService getMulticastServiceMock() {
		if (multicastService != null) return multicastService;

		final ServerPushClientServiceMockImpl serverPushClientServiceMock = getServerPushClientServiceMock();
		return multicastService = new MulticastService() {

			@Override
			public void multicastActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) {
				serverPushClientServiceMock.processIncommingEvent(new ServerActionSyncEvent(modelActionSyncRequest));
			}

			@Override
			public void broadcastProjectCreation(final ProjectRepresentation projectRepresentation) {
				serverPushClientServiceMock.processIncommingEvent(new ProjectCreatedEvent(projectRepresentation));
			}
		};
	}

	public RequestDispatchService getRequestDispatchServiceMock() {
		if (requestDispatchService != null) return requestDispatchService;
		return requestDispatchService = new RequestDispatchService() {

			@Override
			public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
				try {
					getBusinessLogicMock().handleIncomingActionSyncRequest(modelActionSyncRequest);
					dispatchCallback.onRequestCompletition(null);
				}
				catch (final UnableToHandleActionException e) {
					dispatchCallback.onFailure(e);
				}
			}

			@Override
			public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
				try {
					final Project project = getBusinessLogicMock().loadProjectForClient(projectContextRequest);
					dispatchCallback.onRequestCompletition(new ProjectContext(project));
				}
				catch (final Exception e) {
					dispatchCallback.onFailure(e);
				}
			}

			@Override
			public void dispatch(final ProjectCreationRequest projectCreationRequest, final DispatchCallback<ProjectRepresentation> dispatchCallback) {
				try {
					final ProjectRepresentation projectRepresentation = getBusinessLogicMock().createProject(projectCreationRequest.getProjectName());
					dispatchCallback.onRequestCompletition(projectRepresentation);
				}
				catch (final Exception e) {
					dispatchCallback.onFailure(e);
				}
			}

			@Override
			public void dispatch(final ProjectListRequest projectListRequest, final DispatchCallback<List<ProjectRepresentation>> dispatchCallback) {
				try {
					final List<ProjectRepresentation> projectList = getBusinessLogicMock().retrieveProjectList();
					dispatchCallback.onRequestCompletition(projectList);
				}
				catch (final Exception e) {
					dispatchCallback.onFailure(e);
				}
			}
		};
	}

	public ClientIdentificationProvider getClientIdentificationProviderMock() {
		return CLIENT_IDENTIFICATION_PROVIDER;
	}

	public ErrorTreatmentService getErrorTreatmentServiceMock() {
		if (errorTreatmentService != null) return errorTreatmentService;
		return errorTreatmentService = new ErrorTreatmentService() {

			@Override
			public void treatFatalError(final String errorDescriptionMessage) {
				Assert.fail(errorDescriptionMessage);
			}

			@Override
			public void treatFatalError(final String errorDescriptionMessage, final Throwable caught) {
				caught.printStackTrace();
				Assert.fail(errorDescriptionMessage);
			}

			@Override
			public void treatUserWarning(final String string, final Exception e) {
				// Purposefully ignored exception
			}
		};
	}

	public ProjectRepresentationProvider getProjectRepresentationProviderMock() {
		final ProjectRepresentationProviderMock projectRepresentationProvider = new ProjectRepresentationProviderMock();
		projectRepresentationProvider.setProjectRepresentation(new ProjectRepresentation(DEFAULT_PROJECT_ID, "Default project"));
		return projectRepresentationProvider;
	}
}
