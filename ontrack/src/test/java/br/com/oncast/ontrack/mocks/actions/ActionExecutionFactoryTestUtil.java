package br.com.oncast.ontrack.mocks.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentMock;
import br.com.oncast.ontrack.mocks.ContextProviderServiceMock;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class ActionExecutionFactoryTestUtil {

	public static ActionExecutionServiceImpl create(final ProjectContext projectContext) {
		final ContextProviderService contextService = new ContextProviderServiceMock(projectContext);
		final ProjectRepresentationProvider projectRepresentationProvider = mock(ProjectRepresentationProviderImpl.class);
		final ProjectRepresentation projectRepresentation = mock(ProjectRepresentation.class);

		when(projectRepresentationProvider.getCurrentProjectRepresentation()).thenReturn(projectRepresentation);
		when(projectRepresentation.getId()).thenReturn(0L);

		return new ActionExecutionServiceImpl(contextService, new ErrorTreatmentMock(), projectRepresentationProvider);
	}
}
