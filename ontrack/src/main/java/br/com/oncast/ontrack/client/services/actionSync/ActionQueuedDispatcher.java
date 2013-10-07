package br.com.oncast.ontrack.client.services.actionSync;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.events.PendingActionsChangeEvent;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
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

	public ActionQueuedDispatcher(final DispatchService requestDispatchService, final ProjectRepresentationProvider projectRepresentationProvider, final EventBus eventBus,
			final ClientAlertingService alertingService, final ClientStorageService storage, final ClientErrorMessages messages) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;
		this.eventBus = eventBus;
		this.alertingService = alertingService;
		this.storage = storage;
		this.messages = messages;

		actionList = new ArrayList<ModelAction>();

		reverseActionList = new ArrayList<ModelAction>();
		waitingServerAnswerActionList = new ArrayList<ModelAction>();
		Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(final ClosingEvent event) {
				final int nOfPendingActions = actionList.size() + waitingServerAnswerActionList.size();
				savePendingActions();

				if (nOfPendingActions > 0) event.setMessage(messages.thereArePedingActionsWannaLeaveAnyway("" + nOfPendingActions));
			}
		});

	}

	public void dispatch(final ModelAction action, final ActionExecutionContext executionContext) {
		actionList.add(action);
		reverseActionList.add(executionContext.getReverseAction());
		firePendingActionsChangeEvent();
		tryExchange();
	}

	public void tryExchange() {
		tryExchange(false);
	}

	public void tryExchange(final boolean returnActionsToSender) {
		if (paused) return;
		if (!waitingServerAnswerActionList.isEmpty()) return;
		if (actionList.isEmpty()) return;

		waitingServerAnswerActionList = new ArrayList<ModelAction>(actionList);
		final ArrayList<ModelAction> waitingServerAnsuerReverseActionList = new ArrayList<ModelAction>(reverseActionList);
		actionList.removeAll(waitingServerAnswerActionList);
		firePendingActionsChangeEvent();
		reverseActionList.removeAll(waitingServerAnsuerReverseActionList);

		requestDispatchService.dispatch(new ModelActionSyncRequest(projectRepresentationProvider.getCurrent(), waitingServerAnswerActionList).setShouldReturnToSender(returnActionsToSender),
				new DispatchCallback<ModelActionSyncRequestResponse>() {

					@Override
					public void onSuccess(final ModelActionSyncRequestResponse response) {
						waitingServerAnswerActionList.clear();
						firePendingActionsChangeEvent();
						notifyCallbacks(response);
						tryExchange();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {
						resetPendingActions();
					}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
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
						firePendingActionsChangeEvent();
					}
				});
	}

	private void firePendingActionsChangeEvent() {
		eventBus.fireEvent(new PendingActionsChangeEvent(actionList, waitingServerAnswerActionList));
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
	}

	public void loadPendingActions() {
		final List<ModelAction> pendingActions = storage.loadPendingActions();
		if (pendingActions.isEmpty()) return;

		actionList.addAll(pendingActions);
		tryExchange(true);
		savePendingActions();
		if (actionList.isEmpty()) alertingService.showSuccess(messages.pendingActionsSynced(pendingActions.size()), ClientAlertingService.DURATION_LONG);
	}

}
