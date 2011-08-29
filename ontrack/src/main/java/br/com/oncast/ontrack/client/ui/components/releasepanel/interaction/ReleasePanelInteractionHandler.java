package br.com.oncast.ontrack.client.ui.components.releasepanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleasePanelInteractionHandler implements ReleasePanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler applicationActionHandler;

	@Override
	public void onReleaseDeletionRequest(final Release release) {
		assureConfigured();

		// FIXME Create action and send request its execution using applicationActionHandler.
		// applicationActionHandler.onUserActionExecutionRequest(new ReleaseRemoveAction());
	}

	private void assureConfigured() {
		if (applicationActionHandler == null) throw new RuntimeException("This class was not yet configured.");
	}

	public void configure(final ActionExecutionRequestHandler applicationActionHandler) {
		this.applicationActionHandler = applicationActionHandler;
	}
}
