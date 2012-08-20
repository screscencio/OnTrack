package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

public class FileUploadPostProcessingTest {

	@Test
	public void postProcessingShouldUpdateFileRepresentationInPersistence() throws UnableToPostProcessActionException, FileRepresentationNotFoundException,
			PersistenceException {

		final UUID fileRepresentationId = new UUID("1");
		final FileRepresentation fileRepresentation = new FileRepresentation(fileRepresentationId, "fileName", "filePath", new UUID());

		final PersistenceService persistenceServiceMock = Mockito.mock(PersistenceService.class);
		final NotificationService notificationService = Mockito.mock(NotificationService.class);
		final ProjectContext projectContextMock = Mockito.mock(ProjectContext.class);
		Mockito.when(projectContextMock.findFileRepresentation(fileRepresentationId)).thenReturn(fileRepresentation);
		Mockito.when(projectContextMock.getProjectRepresentation()).thenReturn(new ProjectRepresentation(new UUID("1"), ""));

		final FileUploadPostProcessor postProcessing = new FileUploadPostProcessor(persistenceServiceMock, notificationService);
		postProcessing.process(new FileUploadAction(fileRepresentation),
				Mockito.mock(ActionContext.class),
				projectContextMock);

		Mockito.verify(persistenceServiceMock, Mockito.times(1)).persistOrUpdateFileRepresentation(fileRepresentation);
	}

	@Test
	public void postProcessingNotifyUserAutor() throws UnableToPostProcessActionException, FileRepresentationNotFoundException,
			PersistenceException {

		final ProjectContext projectContextMock = Mockito.mock(ProjectContext.class);
		final UUID fileRepresentationId = new UUID("1");
		final FileRepresentation fileRepresentation = new FileRepresentation(fileRepresentationId, "fileName", "filePath", new UUID());
		Mockito.when(projectContextMock.findFileRepresentation(fileRepresentationId)).thenReturn(fileRepresentation);
		Mockito.when(projectContextMock.getProjectRepresentation()).thenReturn(new ProjectRepresentation(new UUID("1"), ""));

		final PersistenceService persistenceServiceMock = Mockito.mock(PersistenceService.class);
		final NotificationService notificationServiceMock = Mockito.mock(NotificationService.class);

		final FileUploadPostProcessor postProcessing = new FileUploadPostProcessor(persistenceServiceMock, notificationServiceMock);
		postProcessing.process(new FileUploadAction(fileRepresentation),
				Mockito.mock(ActionContext.class),
				projectContextMock);

		Mockito.verify(notificationServiceMock, Mockito.times(1)).notifyActionToCurrentUser(Mockito.any(ModelActionSyncEvent.class));
	}

}
