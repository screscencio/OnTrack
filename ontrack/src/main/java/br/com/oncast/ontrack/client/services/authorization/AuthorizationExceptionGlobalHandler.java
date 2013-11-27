package br.com.oncast.ontrack.client.services.authorization;

import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;

public class AuthorizationExceptionGlobalHandler implements FailureHandler<AuthorizationException> {

	private final ApplicationPlaceController applicationPlaceController;
	private final ContextProviderService contextProviderService;

	public AuthorizationExceptionGlobalHandler(final ApplicationPlaceController applicationPlaceController, final ContextProviderService contextProviderService) {
		this.applicationPlaceController = applicationPlaceController;
		this.contextProviderService = contextProviderService;
	}

	@Override
	public void handle(final AuthorizationException caught) {
		if (contextProviderService.isContextAvailable(caught.getProjectId())) contextProviderService.unloadProjectContext();
		applicationPlaceController.goTo(new ProjectSelectionPlace());
	}
}
