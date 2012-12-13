package br.com.oncast.ontrack.utils.mocks.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.ContextProviderServiceMock;

public class ActionExecutionFactoryTestUtil {

	public static ActionExecutionServiceImpl create(final ProjectContext projectContext) throws Exception {
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		final ProjectRepresentationProvider projectRepresentationProvider = mock(ProjectRepresentationProviderImpl.class);
		final ProjectRepresentation projectRepresentation = mock(ProjectRepresentation.class);
		final ApplicationPlaceController applicationPlaceController = mock(ApplicationPlaceController.class);
		final ClientAlertingService alertingService = mock(ClientAlertingService.class);
		final AuthenticationService authenticationService = mock(AuthenticationService.class);

		when(authenticationService.getCurrentUserId()).thenReturn(DefaultAuthenticationCredentials.USER_ID);
		when(projectRepresentationProvider.getCurrent()).thenReturn(projectRepresentation);
		when(projectRepresentation.getId()).thenReturn(UUID.INVALID_UUID);

		return new ActionExecutionServiceImpl(contextService, alertingService, projectRepresentationProvider, applicationPlaceController,
				authenticationService);
	}

	public static ActionExecutionServiceImpl create(final ProjectContext projectContext, final ClientAlertingService alertingService) throws Exception {
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		final ProjectRepresentationProvider projectRepresentationProvider = mock(ProjectRepresentationProviderImpl.class);
		final ProjectRepresentation projectRepresentation = mock(ProjectRepresentation.class);
		final ApplicationPlaceController applicationPlaceController = mock(ApplicationPlaceController.class);
		final AuthenticationService authenticationService = mock(AuthenticationService.class);

		when(authenticationService.getCurrentUserId()).thenReturn(DefaultAuthenticationCredentials.USER_ID);
		when(projectRepresentationProvider.getCurrent()).thenReturn(projectRepresentation);
		when(projectRepresentation.getId()).thenReturn(UUID.INVALID_UUID);

		return new ActionExecutionServiceImpl(contextService, alertingService, projectRepresentationProvider, applicationPlaceController,
				authenticationService);
	}
}
