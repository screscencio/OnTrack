package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.BusinessLogicMockFactoryTestUtils;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
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
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;
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
	private DispatchService DispatchRequestService;
	private ServerPushClientServiceMockImpl serverPushClientService;
	private ActionExecutionService actionExecutionService;
	private BusinessLogic businessLogic;
	private NotificationService notificationService;
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
		return businessLogic = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndCustomNotificationMock(getNotificationServiceMock());
	}

	public NotificationService getNotificationServiceMock() {
		if (notificationService != null) return notificationService;

		final ServerPushClientServiceMockImpl serverPushClientServiceMock = getServerPushClientServiceMock();
		// FIXME Jaime / Matsumoto : Refactor this to a Mockito's Mock
		return notificationService = new NotificationService() {

			@Override
			public void notifyActions(final ModelActionSyncRequest modelActionSyncRequest) {
				serverPushClientServiceMock.processIncommingEvent(new ServerActionSyncEvent(modelActionSyncRequest));
			}

			@Override
			public void notifyProjectCreation(final long userId, final ProjectRepresentation projectRepresentation) {
				}
		};
	}

	public DispatchService getRequestDispatchServiceMock() {
		if (DispatchRequestService != null) return DispatchRequestService;
		return DispatchRequestService = new DispatchService() {

			@Override
			public <T extends FailureHandler<R>, R extends Throwable> void addFailureHandler(final Class<R> throwableClass, final T handler) {
				throw new RuntimeException("The test should not use this method.");
			}

			@Override
			public <T extends DispatchRequest<R>, R extends DispatchResponse> void dispatch(final T request, final DispatchCallback<R> dispatchCallback) {
				answer(request, dispatchCallback);
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			private void answer(final DispatchRequest request, final DispatchCallback dispatchCallback) {
				if (request instanceof ModelActionSyncRequest) {
					try {
						getBusinessLogicMock().handleIncomingActionSyncRequest((ModelActionSyncRequest) request);
						dispatchCallback.onSuccess(null);
					}
					catch (final UnableToHandleActionException e) {
						dispatchCallback.onUntreatedFailure(e);
					}
				}
				else if (request instanceof ProjectListRequest) {
					try {
						final List<ProjectRepresentation> projectList = getBusinessLogicMock().retrieveProjectList();
						dispatchCallback.onSuccess(new ProjectListResponse(projectList));
					}
					catch (final Exception e) {
						dispatchCallback.onUntreatedFailure(e);
					}
				}
				else if (request instanceof ProjectCreationRequest) {
					try {
						final ProjectRepresentation projectRepresentation = getBusinessLogicMock().createProject(
								((ProjectCreationRequest) request).getProjectName());
						dispatchCallback.onSuccess(new ProjectCreationResponse(projectRepresentation));
					}
					catch (final Exception e) {
						dispatchCallback.onUntreatedFailure(e);
					}
				}
				else if (request instanceof ProjectContextRequest) {
					try {
						final Project project = getBusinessLogicMock().loadProjectForClient((ProjectContextRequest) request);
						dispatchCallback.onSuccess(new ProjectContextResponse(project));
					}
					catch (final Exception e) {
						dispatchCallback.onUntreatedFailure(e);
					}
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
