package br.com.oncast.ontrack.client.services.actionSync;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.events.PendingActionsCountChangeEvent;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequestResponse;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.web.bindery.event.shared.EventBus;

class ActionQueuedDispatcher {

	private final DispatchService requestDispatchService;

	private final List<ModelAction> actionList;
	private final List<ModelAction> reverseActionList;
	private List<ModelAction> waitingServerAnswerActionList;
	private final ProjectRepresentationProvider projectRepresentationProvider;
	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private final List<ActionQueuedDispatchCallback> callbacks = new ArrayList<ActionQueuedDispatchCallback>();
	private boolean paused = false;

	private final EventBus eventBus;

	private final ClientStorageService storage;

	private final ClientMetricsService metrics;

	public ActionQueuedDispatcher(final DispatchService requestDispatchService, final ProjectRepresentationProvider projectRepresentationProvider, final EventBus eventBus,
			final ClientAlertingService alertingService, final ClientErrorMessages messages, final ClientStorageService clientStorageService, final ClientMetricsService metrics) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;
		this.eventBus = eventBus;
		this.alertingService = alertingService;
		this.messages = messages;
		storage = clientStorageService;
		this.metrics = metrics;

		actionList = new ArrayList<ModelAction>();
		reverseActionList = new ArrayList<ModelAction>();
		waitingServerAnswerActionList = new ArrayList<ModelAction>();
		Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(final ClosingEvent event) {
				final int nOfPendingActions = actionList.size() + waitingServerAnswerActionList.size();
				if (projectRepresentationProvider.hasAvailableProjectRepresentation()) savePendingActions();
				metrics.onClientClose(nOfPendingActions);
				if (nOfPendingActions > 0) event.setMessage(messages.thereArePedingActionsWannaLeaveAnyway("" + nOfPendingActions));
			}
		});
	}

	public void dispatch(final ModelAction action, final ActionExecutionContext executionContext) {
		metrics.onActionExecution(action, !paused);
		actionList.add(action);
		reverseActionList.add(executionContext.getReverseAction());
		firePendingActionsCountChangeEvent();
		tryExchange();
	}

	public void tryExchange() {
		tryExchange(false);
	}

	public void tryExchange(final boolean returnActionsToSender) {
		if (paused) return;
		if (!waitingServerAnswerActionList.isEmpty()) return;
		if (actionList.isEmpty()) return;

		waitingServerAnswerActionList = getActionsBatch();
		final int size = waitingServerAnswerActionList.size();
		final ArrayList<ModelAction> waitingServerAnsuerReverseActionList = new ArrayList<ModelAction>(reverseActionList.subList(0, size > reverseActionList.size() ? reverseActionList.size() : size));
		actionList.removeAll(waitingServerAnswerActionList);
		firePendingActionsCountChangeEvent();
		reverseActionList.removeAll(waitingServerAnsuerReverseActionList);

		// TODO Display 'loading' UI indicator.
		requestDispatchService.dispatch(new ModelActionSyncRequest(projectRepresentationProvider.getCurrent(), waitingServerAnswerActionList).setShouldReturnToSender(returnActionsToSender),
				new DispatchCallback<ModelActionSyncRequestResponse>() {

					@Override
					public void onSuccess(final ModelActionSyncRequestResponse response) {
						// TODO Hide 'loading' UI indicator.
						waitingServerAnswerActionList.clear();
						firePendingActionsCountChangeEvent();
						notifyCallbacks(response);
						tryExchange();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {
						resetPendingActions();
					}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						// TODO Hide 'loading' UI indicator.
						// TODO When "Broswer-Reload" is removed, this method should fix "sync lists" according to the error returned.
						// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
						if (caught instanceof InvalidIncomingAction || caught instanceof UnableToHandleActionException) {
							waitingServerAnswerActionList.clear();
							savePendingActions();
							alertingService.showErrorWithConfirmation(messages.projectOutOfSync(), new AlertConfirmationListener() {
								@Override
								public void onConfirmation() {
									Window.Location.reload();
								}
							});
						} else {
							resetPendingActions();
						}
					}

					private void resetPendingActions() {
						for (final ModelAction pendingReverseAction : waitingServerAnsuerReverseActionList) {
							reverseActionList.add(0, pendingReverseAction);
						}

						for (final ModelAction pendingAction : waitingServerAnswerActionList) {
							actionList.add(0, pendingAction);
						}
						waitingServerAnswerActionList.clear();
						firePendingActionsCountChangeEvent();
					}
				});
	}

	private ArrayList<ModelAction> getActionsBatch() {
		final ArrayList<ModelAction> list = new ArrayList<ModelAction>();
		for (final ModelAction action : new ArrayList<ModelAction>(actionList)) {
			if (!list.isEmpty() && action instanceof ScopeBindReleaseAction) break;
			list.add(action);
		}
		return list;
	}

	private void firePendingActionsCountChangeEvent() {
		eventBus.fireEvent(new PendingActionsCountChangeEvent(actionList.size(), waitingServerAnswerActionList.size()));
	}

	protected void notifyCallbacks(final ModelActionSyncRequestResponse response) {
		for (final ActionQueuedDispatchCallback callback : callbacks) {
			callback.onDispatch(response.getLastApplyedActionId());
		}
	}

	public void addDispatchCallback(final ActionQueuedDispatchCallback actionQueuedDispatchCallback) {
		callbacks.add(actionQueuedDispatchCallback);
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public List<ModelAction> getPendingReverseActions() {
		return ImmutableList.copyOf(reverseActionList);
	}

	private void savePendingActions() {
		final List<ModelAction> pendingActions = new ArrayList<ModelAction>(waitingServerAnswerActionList);
		pendingActions.addAll(actionList);
		storage.savePendingActions(pendingActions);
		if (!pendingActions.isEmpty()) metrics.onPendingActionsSavedLocally(pendingActions.size());
	}

	public void loadPendingActions() {
		final List<ModelAction> pendingActions = storage.loadPendingActions();
		if (pendingActions == null || pendingActions.isEmpty()) return;

		metrics.onLocallySavedPendingActionsLoaded(pendingActions.size());
		actionList.addAll(pendingActions);
		tryExchange(true);
		savePendingActions();

		metrics.onLocallySavedPendingActionsSync(actionList.isEmpty(), pendingActions.size());
		if (actionList.isEmpty()) alertingService.showSuccess(messages.pendingActionsSynced(pendingActions.size()), ClientAlertingService.DURATION_LONG);
	}

}
