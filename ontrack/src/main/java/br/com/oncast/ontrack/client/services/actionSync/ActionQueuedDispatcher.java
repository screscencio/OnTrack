package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

class ActionQueuedDispatcher {

	private final DispatchService requestDispatchService;

	private final List<ModelAction> actionList;
	private List<ModelAction> waitingServerAnswerActionList;
	private final ErrorTreatmentService errorTreatmentService;
	private final ProjectRepresentationProvider projectRepresentationProvider;

	public ActionQueuedDispatcher(final DispatchService requestDispatchService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ErrorTreatmentService errorTreatmentService) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.errorTreatmentService = errorTreatmentService;
		this.requestDispatchService = requestDispatchService;

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
		requestDispatchService.dispatch(
				new ModelActionSyncRequest(projectRepresentationProvider.getCurrentProjectRepresentation(), waitingServerAnswerActionList),
				new DispatchCallback<VoidResult>() {

					@Override
					public void onSuccess(final VoidResult response) {
						// TODO Hide 'loading' UI indicator.
						waitingServerAnswerActionList.clear();
						sync();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						// TODO Hide 'loading' UI indicator.
						// TODO When "Broswer-Reload" is removed, this method should fix "sync lists" according to the error returned.
						// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
						if (caught instanceof InvalidIncomingAction || caught instanceof UnableToHandleActionException) {
							errorTreatmentService
									.treatFatalError(
											"The application is out of sync with the server.\nA conflict between multiple client's states was detected.\n\nIt will be briethly reloaded and some of your lattest changes may be rollbacked.",
											caught);
						}
						else {
							errorTreatmentService.treatConnectionError(
									"The application server is unreachable.\nCheck your internet connection.\n\nThe application will be briethly reloaded",
									caught);
							caught.printStackTrace();
						}
					}
				});
	}
}
