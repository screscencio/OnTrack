package br.com.oncast.ontrack.client.services.actionSync;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.ui.events.ActionsDispatchEvent;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequestResponse;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

public class QueuedActionsDispatcher {

	private final DispatchService dispatcher;

	private final List<ActionDispatcherListener> listeners;

	private final List<UserAction> waitingToBeDispatched;
	private List<UserAction> waitingForAcknowledgement;

	private final ClientMetricsService metrics;

	private final EventBus eventBus;

	private final ProjectRepresentationProvider projectRepresentationProvider;

	public QueuedActionsDispatcher(final DispatchService dispatchService, final ProjectRepresentationProvider projectRepresentationProvider, final ClientMetricsService metrics, final EventBus eventBus) {
		this.dispatcher = dispatchService;
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.metrics = metrics;
		this.eventBus = eventBus;
		this.waitingToBeDispatched = new ArrayList<UserAction>();
		this.waitingForAcknowledgement = new ArrayList<UserAction>();
		this.listeners = new ArrayList<ActionDispatcherListener>();
	}

	public HandlerRegistration registerListener(final ActionDispatcherListener listener) {
		listeners.add(listener);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				listeners.remove(listener);
			}
		};
	}

	public void dispatch(final UserAction action) {
		waitingToBeDispatched.add(action);
		tryExchange();
	}

	private void tryExchange() {
		if (!waitingForAcknowledgement.isEmpty() || waitingToBeDispatched.isEmpty()) return;

		waitingForAcknowledgement = getActionsBatch();
		eventBus.fireEvent(new ActionsDispatchEvent(true, waitingForAcknowledgement.size()));
		waitingToBeDispatched.removeAll(waitingForAcknowledgement);

		final TimeTrackingEvent timeTracking = metrics.startTimeTracking(MetricsCategories.ACTIONS_DISPATCH, "" + waitingForAcknowledgement.size());
		dispatcher.dispatch(new ModelActionSyncRequest(getCurrentProjectId(), waitingForAcknowledgement), new DispatchCallback<ModelActionSyncRequestResponse>() {

			@Override
			public void onSuccess(final ModelActionSyncRequestResponse response) {
				timeTracking.end();
				notifyCallbackForSuccess(waitingForAcknowledgement, response);
				clearWaitingForAcknowledgementList();
				tryExchange();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				resetPendingActions();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				if (caught instanceof UnableToHandleActionException) {
					timeTracking.end();
					notifyCallbackForConflicts(waitingForAcknowledgement, (UnableToHandleActionException) caught);
					clearWaitingForAcknowledgementList();
				} else {
					resetPendingActions();
				}
			}

			private void resetPendingActions() {
				for (final UserAction pendingAction : waitingForAcknowledgement) {
					waitingToBeDispatched.add(0, pendingAction);
				}
				clearWaitingForAcknowledgementList();
			}

			private void clearWaitingForAcknowledgementList() {
				eventBus.fireEvent(new ActionsDispatchEvent(false, waitingForAcknowledgement.size()));
				waitingForAcknowledgement.clear();
			}
		});
	}

	private UUID getCurrentProjectId() {
		return projectRepresentationProvider.getCurrent().getId();
	}

	private ArrayList<UserAction> getActionsBatch() {
		final ArrayList<UserAction> list = new ArrayList<UserAction>();
		for (final UserAction action : new ArrayList<UserAction>(waitingToBeDispatched)) {
			if (!list.isEmpty() && action.getModelAction() instanceof ScopeBindReleaseAction) break;
			list.add(action);
		}
		return list;
	}

	public void retrieveAllActionsSince(final UUID projectId, final long lastSyncedActionsId, final AsyncCallback<ModelActionSyncEvent> callback) {
		final TimeTrackingEvent tracking = metrics.startTimeTracking(MetricsCategories.ACTIONS_FETCH, projectId.toString());
		dispatcher.dispatch(new ModelActionSyncEventRequest(projectId, lastSyncedActionsId), new DispatchCallback<ModelActionSyncEventRequestResponse>() {
			@Override
			public void onSuccess(final ModelActionSyncEventRequestResponse result) {
				tracking.setLabel("" + result.getModelActionSyncEvent().getActionList().size()).end();
				callback.onSuccess(result.getModelActionSyncEvent());
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

		});
	}

	private void notifyCallbackForSuccess(final List<UserAction> sentActions, final ModelActionSyncRequestResponse response) {
		for (final ActionDispatcherListener listener : listeners) {
			listener.onActionsAcceptedByServer(new ArrayList<UserAction>(sentActions), response.getLastApplyedActionId());
		}
	}

	private void notifyCallbackForConflicts(final List<UserAction> conflictedActions, final UnableToHandleActionException exception) {
		for (final ActionDispatcherListener listener : listeners) {
			listener.onActionsRegectedByServer(new ArrayList<UserAction>(conflictedActions), exception);
		}
	}

	public interface ActionDispatcherListener {

		void onActionsAcceptedByServer(List<UserAction> sentActions, long actionSyncId);

		void onActionsRegectedByServer(List<UserAction> regectedActions, UnableToHandleActionException error);

	}

}
