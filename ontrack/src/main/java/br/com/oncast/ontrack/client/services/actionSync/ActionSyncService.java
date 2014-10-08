package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.client.i18n.ClientMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionSync.QueuedActionsDispatcher.ActionDispatcherListener;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ContextChangeListener;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.internet.ConnectionListener;
import br.com.oncast.ontrack.client.services.internet.NetworkMonitoringService;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

public class ActionSyncService implements ActionExecutionListener, ConnectionListener, ContextChangeListener, ServerActionSyncEventHandler, ActionDispatcherListener {

	private static final int NOT_SYNCED = 0;

	private final QueuedActionsDispatcher dispatcher;

	private boolean paused;

	private long lastSyncedActionsId;

	private final ActionExecutionService actionExecutionService;

	private final ClientAlertingService alertingService;

	private PersistedActionExecutionContextsList syncList;

	private UUID currentProjectId;

	private final ClientStorageService storage;

	private final ClientMetricsService metrics;

	private final EventBus eventBus;

	private final ClientMessages messages;

	public ActionSyncService(final QueuedActionsDispatcher dispatcher, final ActionExecutionService actionExecutionService, final ClientStorageService storage,
			final ClientAlertingService alertingService, final ClientMessages messages, final NetworkMonitoringService networkMonitoringService, final ContextProviderService contextProviderService,
			final ServerPushClientService serverPushService, final ClientMetricsService metrics, final EventBus eventBus) {
		this.dispatcher = dispatcher;
		this.actionExecutionService = actionExecutionService;
		this.storage = storage;
		this.alertingService = alertingService;
		this.messages = messages;
		this.metrics = metrics;
		this.eventBus = eventBus;
		this.paused = false;
		this.lastSyncedActionsId = NOT_SYNCED;

		dispatcher.registerListener(this);
		actionExecutionService.addActionExecutionListener(this);
		networkMonitoringService.addConnectionListener(this);
		contextProviderService.addContextLoadListener(this);
		serverPushService.registerServerEventHandler(ModelActionSyncEvent.class, this);
	}

	@Override
	public void onActionExecution(final ActionExecutionContext executionContext, final ProjectContext context, final boolean isUserAction) {
		if (!isUserAction) return;

		syncList.add(executionContext);
		final UserAction action = executionContext.getUserAction();
		metrics.onActionExecution(action, !paused);
		if (paused) return;

		dispatch(action);
	}

	@Override
	public void onConnectionRecovered() {
		metrics.onConnectionRecovered();
		if (!paused) return;

		final AlertRegistration alertRegistration = alertingService.showInfo(messages.syncing());
		if (!revertLocalPendingActions()) return;
		dispatcher.retrieveAllActionsSince(currentProjectId, lastSyncedActionsId, new AsyncCallback<ModelActionSyncEvent>() {

			@Override
			public void onFailure(final Throwable caught) {
				alertRegistration.hide();
				showFatalError(messages.couldNotRetrieveLatestModifications());
			}

			@Override
			public void onSuccess(final ModelActionSyncEvent event) {
				if (handleIncomingActions(event)) {
					applyLocalPendingActions();
					alertRegistration.hide();
				}
				paused = false;
			}

		});
	}

	// TODO+++ instead of clearing the entire syncList, ask the user if he wants to skip the conflicted action and continue to re-sync
	private void applyLocalPendingActions() {
		for (final ActionExecutionContext entry : syncList) {
			try {
				actionExecutionService.onNonUserActionRequest(entry.getUserAction());
			} catch (final UnableToCompleteActionException e) {
				metrics.onActionConflict(entry.getUserAction(), e);
				syncList.clear();
				showError(messages.someChangesConflicted());
				return;
			}
			dispatch(entry.getUserAction());
		}
		if (!syncList.isEmpty()) alertingService.showSuccess(messages.pendingActionsSynced(syncList.size()));
	}

	@Override
	public void onConnectionLost() {
		metrics.onConnectionLost();
		this.paused = true;
	}

	@Override
	public void onProjectChanged(final UUID projectId, final Long loadedProjectRevision) {
		currentProjectId = projectId;
		lastSyncedActionsId = loadedProjectRevision == null ? NOT_SYNCED : loadedProjectRevision;
		syncList = new PersistedActionExecutionContextsList(projectId, storage, eventBus);
		metrics.onLocallySavedPendingActionsLoaded(syncList.size());
		applyLocalPendingActions();
	}

	private boolean revertLocalPendingActions() {
		for (final ActionExecutionContext entry : syncList.reverse()) {
			if (!applyLocally(entry.getReverseUserAction())) return false;
		}
		return true;
	}

	private boolean applyLocally(final UserAction action) {
		try {
			actionExecutionService.onNonUserActionRequest(action);
			return true;
		} catch (final UnableToCompleteActionException e) {
			metrics.onException("ActionSyncService.applyLocally(" + MetricsTokenizer.getClassSimpleName(action) + ")" + e.getMessage());
			showFatalError(messages.projectOutOfSync());
			return false;
		}
	}

	private void showFatalError(final String message) {
		alertingService.showErrorWithConfirmation(message, new AlertConfirmationListener() {
			@Override
			public void onConfirmation() {
				Window.Location.reload();
			}
		});
	}

	private void showError(final String message) {
		alertingService.showError(message);
	}

	private void dispatch(final UserAction action) {
		dispatcher.dispatch(action);
	}

	@Override
	public void onEvent(final ModelActionSyncEvent event) {
		handleIncomingActions(event);
	}

	private boolean handleIncomingActions(final ModelActionSyncEvent event) {
		for (final UserAction action : event.getActionList()) {
			if (!applyLocally(action)) return false;
		}
		updateLastSyncedActionsId(event.getLastActionId());
		return true;
	}

	@Override
	public void onActionsAcceptedByServer(final List<UserAction> sentActions, final long actionSyncId) {
		for (final UserAction action : sentActions) {
			syncList.remove(action);
		}
		updateLastSyncedActionsId(actionSyncId);
	}

	private void updateLastSyncedActionsId(final long syncId) {
		if (lastSyncedActionsId < syncId) lastSyncedActionsId = syncId;
	}

	@Override
	public void onActionsRegectedByServer(final List<UserAction> regectedActions, final UnableToHandleActionException error) {
		for (final UserAction action : Lists.reverse(regectedActions)) {
			final ActionExecutionContext entry = syncList.remove(action);
			applyLocally(entry.getReverseUserAction());
		}
	}

}