package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.communication.CommunicationService;
import br.com.oncast.ontrack.client.services.communication.DispatchCallback;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.services.communication.ModelActionSyncRequest;

public class ActionSyncService {

	// TODO Configure a communication channel with a listener for server-client pushed actions
	public ActionSyncService(final CommunicationService communicationService, final ActionExecutionService actionExecutionService) {
		final ActionExecutionListener actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ScopeAction action, final ProjectContext context, final boolean wasRollback) {
				// TODO Display 'loading' UI indicator.
				communicationService.dispatch(new ModelActionSyncRequest(action), new DispatchCallback<Void>() {

					@Override
					public void onRequestCompletition(final Void response) {
						// TODO Hide 'loading' UI indicator.
					}

					@Override
					public void onFailure(final Throwable caught) {
						// TODO Hide 'loading' UI indicator.
						// TODO Treat communication failure.
					}
				});
			}
		};
		actionExecutionService.addActionExecutionListener(actionExecutionListener);
	}

}
