package br.com.oncast.ontrack.client.services.authorization;

import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;

public class AuthorizationExceptionGlobalHandler implements FailureHandler<AuthorizationException> {

	private final ApplicationPlaceController applicationPlaceController;

	public AuthorizationExceptionGlobalHandler(final ApplicationPlaceController applicationPlaceController) {
		this.applicationPlaceController = applicationPlaceController;
	}

	@Override
	public void handle(final AuthorizationException caught) {
		applicationPlaceController.goTo(new ProjectSelectionPlace());
	}
}
