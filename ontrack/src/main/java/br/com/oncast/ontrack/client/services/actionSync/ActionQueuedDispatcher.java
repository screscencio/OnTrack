package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequestResponse;

import com.google.gwt.user.client.Window;

class ActionQueuedDispatcher {

	private final DispatchService requestDispatchService;

	private final List<ModelAction> actionList;
	private List<ModelAction> waitingServerAnswerActionList;
	private final ProjectRepresentationProvider projectRepresentationProvider;
	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private final List<ActionQueuedDispatchCallback> callbacks = new ArrayList<ActionQueuedDispatchCallback>();

	public ActionQueuedDispatcher(final DispatchService requestDispatchService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ClientAlertingService alertingService, final ClientErrorMessages messages) {

		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;
		this.alertingService = alertingService;
		this.messages = messages;

		actionList = new ArrayList<ModelAction>();
		waitingServerAnswerActionList = new ArrayList<ModelAction>();
	}

	public void dispatch(final ModelAction action) {
		actionList.add(action);
		tryExchange();
	}

	public void tryExchange() {
		if (!waitingServerAnswerActionList.isEmpty()) return;
		if (actionList.isEmpty()) return;

		waitingServerAnswerActionList = new ArrayList<ModelAction>(actionList);
		actionList.removeAll(waitingServerAnswerActionList);

		// TODO Display 'loading' UI indicator.
		requestDispatchService.dispatch(
				new ModelActionSyncRequest(projectRepresentationProvider.getCurrent(), waitingServerAnswerActionList),
				new DispatchCallback<ModelActionSyncRequestResponse>() {

					@Override
					public void onSuccess(final ModelActionSyncRequestResponse response) {
						// TODO Hide 'loading' UI indicator.
						waitingServerAnswerActionList.clear();
						notifyCallbacks(response);
						tryExchange();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						// TODO Hide 'loading' UI indicator.
						// TODO When "Broswer-Reload" is removed, this method should fix "sync lists" according to the error returned.
						// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
						if (caught instanceof InvalidIncomingAction || caught instanceof UnableToHandleActionException) {
							alertingService
									.showErrorWithConfirmation(
											messages.projectOutOfSync(),
											new AlertConfirmationListener() {
												@Override
												public void onConfirmation() {
													Window.Location.reload();
												}
											});
						}
						else {
							alertingService.showErrorWithConfirmation(messages.connectionLost(), new AlertConfirmationListener() {
								@Override
								public void onConfirmation() {
									Window.Location.reload();
								}
							});
						}
					}
				});
	}

	protected void notifyCallbacks(final ModelActionSyncRequestResponse response) {
		for (final ActionQueuedDispatchCallback callback : callbacks) {
			callback.onDispatch(response.getLastApplyedActionId());
		}
	}

	public void addDispatchCallback(final ActionQueuedDispatchCallback actionQueuedDispatchCallback) {
		callbacks.add(actionQueuedDispatchCallback);
	}
}
