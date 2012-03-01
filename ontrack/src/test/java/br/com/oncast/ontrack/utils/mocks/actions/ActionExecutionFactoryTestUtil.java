package br.com.oncast.ontrack.utils.mocks.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentMock;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.utils.mocks.ContextProviderServiceMock;

public class ActionExecutionFactoryTestUtil {

	public static ActionExecutionServiceImpl create(final ProjectContext projectContext) {
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		final ProjectRepresentationProvider projectRepresentationProvider = mock(ProjectRepresentationProviderImpl.class);
		final ProjectRepresentation projectRepresentation = mock(ProjectRepresentation.class);
		final ApplicationPlaceController applicationPlaceController = mock(ApplicationPlaceController.class);

		when(projectRepresentationProvider.getCurrent()).thenReturn(projectRepresentation);
		when(projectRepresentation.getId()).thenReturn(0L);

		return new ActionExecutionServiceImpl(contextService, new ErrorTreatmentMock(), projectRepresentationProvider, applicationPlaceController);
	}
}
