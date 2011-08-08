package br.com.oncast.ontrack.client.services.actionSync;

import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.communication.CommunicationService;
import br.com.oncast.ontrack.client.services.communication.DispatchCallback;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.communication.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private boolean active;

	// TODO Configure a communication channel with a listener for server-client pushed actions
	public ActionSyncService(final CommunicationService communicationService, final ActionExecutionService actionExecutionService) {
		final ActionExecutionListener actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet) {
				if (!active) return;

				// TODO Display 'loading' UI indicator.
				communicationService.dispatch(new ModelActionSyncRequest(action), new DispatchCallback<Void>() {

					@Override
					public void onRequestCompletition(final Void response) {
						// TODO Hide 'loading' UI indicator.
					}

					@Override
					public void onFailure(final Throwable caught) {
						// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
						if (caught instanceof InvalidIncomingAction) {
							Window.alert("An error ocurred while syncing actions with the server: \nAn invalid action was found. \n\nThe application will be briethly reloaded and some of your lattest changes may be rollbacked.");
							Window.Location.reload();
						}
						else {
							// TODO Hide 'loading' UI indicator.
							// TODO +++Treat communication failure.
							caught.printStackTrace();
						}
					}
				});
			}
		};
		actionExecutionService.addActionExecutionListener(actionExecutionListener);
	}

	// TODO Review the necessity of this method, that was created only to make implicit when the service is active or not.
	public void setActive(final boolean active) {
		this.active = active;
	}

}
