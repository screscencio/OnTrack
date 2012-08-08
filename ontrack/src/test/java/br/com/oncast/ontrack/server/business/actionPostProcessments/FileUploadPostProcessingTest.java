package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class FileUploadPostProcessingTest {

	@Test
	public void postProcessingShouldUpdateFileRepresentationInPersistence() throws UnableToPostProcessActionException, FileRepresentationNotFoundException,
			PersistenceException {

		final UUID fileRepresentationId = new UUID("1");
		final FileRepresentation fileRepresentation = new FileRepresentation(fileRepresentationId, "fileName", "filePath", new UUID());

		final PersistenceService persistenceServiceMock = Mockito.mock(PersistenceService.class);
		final ProjectContext projectContextMock = Mockito.mock(ProjectContext.class);
		Mockito.when(projectContextMock.findFileRepresentation(fileRepresentationId)).thenReturn(fileRepresentation);

		final FileUploadPostProcessing postProcessing = new FileUploadPostProcessing(persistenceServiceMock);
		postProcessing.process(new FileUploadAction(fileRepresentation),
				Mockito.mock(ActionContext.class),
				projectContextMock);

		Mockito.verify(persistenceServiceMock, Mockito.times(1)).persistOrUpdateFileRepresentation(fileRepresentation);
	}
}
