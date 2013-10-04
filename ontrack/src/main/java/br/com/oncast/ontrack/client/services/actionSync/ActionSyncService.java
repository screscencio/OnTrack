package br.com.oncast.ontrack.client.services.actionSync;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.internet.ConnectionListener;
import br.com.oncast.ontrack.client.services.internet.NetworkMonitoringService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

public class ActionSyncService {

	private final ClientErrorMessages messages;

	private final ActionQueuedDispatcher actionQueuedDispatcher;

	private final ActionExecutionService actionExecutionService;

	private final ClientAlertingService alertingService;

	private final ProjectRepresentationProvider projectRepresentationProvider;

	private final DispatchService requestDispatchService;

	private Long lastSyncId = null;

	private ProjectContext lastContext = null;

	private final ContextProviderService contextProviderService;

	public ActionSyncService(final DispatchService requestDispatchService, final ServerPushClientService serverPushClientService, final ActionExecutionService actionExecutionService,
			final ProjectRepresentationProvider projectRepresentationProvider, final ClientAlertingService alertingService, final ClientErrorMessages messages,
			final NetworkMonitoringService networkMonitoringService, final ContextProviderService contextProviderService, final EventBus eventBus, final ClientStorageService storage) {
		this.requestDispatchService = requestDispatchService;
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.actionExecutionService = actionExecutionService;
		this.alertingService = alertingService;
		this.messages = messages;
		this.contextProviderService = contextProviderService;
		this.actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchService, projectRepresentationProvider, eventBus, alertingService, storage, messages);

		serverPushClientService.registerServerEventHandler(ModelActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ModelActionSyncEvent event) {
				processServerActionSyncEvent(event);
			}
		});
		this.actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
					final boolean isUserAction) {
				handleActionExecution(action, executionContext, isUserAction);
			}
		});
		networkMonitoringService.addConnectionListener(new ConnectionListener() {
			@Override
			public void onConnectionRecovered() {
				actionQueuedDispatcher.resume();
				requestResyncronization();
			}

			@Override
			public void onConnectionLost() {
				actionQueuedDispatcher.pause();
			}
		});
		contextProviderService.addContextLoadListener(new ContextChangeListener() {
			@Override
			public void onProjectChanged(final UUID projectId, final Long loadedProjectRevision) {
				updateLastSyncId(loadedProjectRevision);
				if (projectId != null) actionQueuedDispatcher.loadPendingActions();
			}
		});
		actionQueuedDispatcher.addDispatchCallback(new ActionQueuedDispatchCallback() {
			@Override
			public void onDispatch(final long applyedActionSyncId) {
				updateLastSyncId(applyedActionSyncId);
			}
		});
	}

	private void processServerActionSyncEvent(final ModelActionSyncEvent event) {
		checkIfRequestIsPertinentToCurrentProject(event);

		try {
			final ActionContext actionContext = event.getActionContext();
			for (final ModelAction modelAction : event.getActionList()) {
				actionExecutionService.onNonUserActionRequest(modelAction, actionContext);
			}
			updateLastSyncId(event.getLastActionId());
		} catch (final UnableToCompleteActionException e) {
			showFatalError(messages.someChangesConflicted());
		}
	}

	private void handleActionExecution(final ModelAction action, final ActionExecutionContext executionContext, final boolean isUserAction) {
		if (!isUserAction) return;
		actionQueuedDispatcher.dispatch(action, executionContext);
	}

	private void checkIfRequestIsPertinentToCurrentProject(final ModelActionSyncEvent event) {
		final UUID requestedProjectId = event.getProjectId();
		final UUID currentProjectId = projectRepresentationProvider.getCurrent().getId();
		if (!requestedProjectId.equals(currentProjectId))
			throw new RuntimeException("This client received an action for project '" + requestedProjectId + "' but it is currently on project '" + currentProjectId + "'. Please notify OnTrack team.");
	}

	private void updateLastSyncId(final Long applyedActionSyncId) {
		lastSyncId = applyedActionSyncId;
		lastContext = applyedActionSyncId == null ? null : contextProviderService.getCurrent();
	}

	protected void requestResyncronization() {
		if (lastSyncId == null) {
			actionQueuedDispatcher.tryExchange();
			return;
		}

		revertToPeviousServerState();
		requestDispatchService.dispatch(new ModelActionSyncEventRequest(projectRepresentationProvider.getCurrent().getId(), lastSyncId), new DispatchCallback<ModelActionSyncEventRequestResponse>() {
			@Override
			public void onSuccess(final ModelActionSyncEventRequestResponse result) {
				processServerActionSyncEvent(result.getModelActionSyncEvent());
				actionQueuedDispatcher.tryExchange(true);
				alertingService.showSuccess(messages.resyncSuccess());
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				showFatalError(messages.connectionLost());
			}

		});
	}

	private void revertToPeviousServerState() {
		final List<ModelAction> pendingActions = actionQueuedDispatcher.getPendingReverseActions();
		if (pendingActions.isEmpty()) return;
		try {
			// IMPORTANT to revert view state
			for (final ModelAction pendingAction : pendingActions) {
				actionExecutionService.onNonUserActionRequest(pendingAction);
			}
			if (lastContext != null) contextProviderService.revertContext(lastContext);
		} catch (final UnableToCompleteActionException e) {
			showFatalError(messages.someChangesConflicted());
		}
	}

	private void showFatalError(final String errorMessage) {
		alertingService.showErrorWithConfirmation(errorMessage, new AlertConfirmationListener() {
			@Override
			public void onConfirmation() {
				Window.Location.reload();
			}
		});
	}
}
