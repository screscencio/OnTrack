package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

class ActionQueuedDispatcher {

	private final RequestDispatchService requestDispatchService;
	private final ClientIdentificationProvider clientIdentificationProvider;

	private final List<ModelAction> actionList;
	private List<ModelAction> waitingServerAnswerActionList;

	public ActionQueuedDispatcher(final RequestDispatchService requestDispatchService, final ClientIdentificationProvider clientIdentificationProvider) {
		this.requestDispatchService = requestDispatchService;
		this.clientIdentificationProvider = clientIdentificationProvider;

		actionList = new ArrayList<ModelAction>();
		waitingServerAnswerActionList = new ArrayList<ModelAction>();
	}

	public void dispatch(final ModelAction action) {
		actionList.add(action);
		sync();
	}

	private void sync() {
		if (!waitingServerAnswerActionList.isEmpty()) return;
		if (actionList.isEmpty()) return;

		waitingServerAnswerActionList = new ArrayList<ModelAction>(actionList);
		actionList.removeAll(waitingServerAnswerActionList);

		// TODO Display 'loading' UI indicator.
		requestDispatchService.dispatch(new ModelActionSyncRequest(clientIdentificationProvider.getClientId(),
				waitingServerAnswerActionList), new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				// TODO Hide 'loading' UI indicator.
				waitingServerAnswerActionList.clear();
				sync();
			}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Hide 'loading' UI indicator.

				// TODO When "Broswer-Reload" is removed, this method should fix "sync lists" according to the error returned.

				// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
				if (caught instanceof InvalidIncomingAction || caught instanceof UnableToHandleActionException) {
					threatSyncingError("The application is out of sync with the server.\nA conflict between multiple client's states was detected.\n\nIt will be briethly reloaded and some of your lattest changes may be rollbacked.");
				}
				else {
					// TODO +++Treat communication failure.
					// TODO +++Notify Error treatment service.
					threatSyncingError("The application server is unreachable.\nCheck your internet connection.\n\nThe application will be briethly reloaded");
					caught.printStackTrace();
				}
			}
		});
	}

	private void threatSyncingError(final String message) {
		// TODO +++Delegate treatment to Error threatment service eliminating the need for this method.
		Window.alert(message);
		Window.Location.reload();
	}
}
