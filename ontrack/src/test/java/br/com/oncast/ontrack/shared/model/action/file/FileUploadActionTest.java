package br.com.oncast.ontrack.shared.model.action.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.file.FileUploadActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class FileUploadActionTest extends ModelActionTest {

	private String fileName;

	private UUID fileId;

	private String filePath;

	private UUID projectUuid;

	@Before
	public void setup() {
		fileName = "fileName.extension";
		fileId = new UUID();
		filePath = "path/to/file";
		projectUuid = new UUID();

		when(context.getProjectRepresentation()).thenReturn(ProjectTestUtils.createRepresentation(projectUuid));
	}

	@Test
	public void shouldCreateAFileRepresentationWithSameName() throws Exception {
		execute();

		final FileRepresentation representation = getAddedFile();
		assertEquals(fileName, representation.getFileName());
	}

	@Test
	public void shouldCreateAFileRepresentationWithSameId() throws Exception {
		execute();

		final FileRepresentation representation = getAddedFile();
		assertEquals(fileId, representation.getId());
	}

	@Test
	public void shouldCreateAFileRepresentationWithSameFilePath() throws Exception {
		execute();

		final FileRepresentation representation = getAddedFile();
		assertEquals(filePath, representation.getFilePath());
	}

	@Test
	public void shouldCreateAFileRepresentationWithSameProject() throws Exception {
		execute();

		final FileRepresentation representation = getAddedFile();
		assertEquals(projectUuid, representation.getProjectId());
	}

	@Test
	public void shouldNotBeAbleToUndoThisAction() throws Exception {
		assertNull(execute());
	}

	@Test
	public void shouldBeAbleToCreateFromAnExistingFileRepresentation() throws Exception {
		final FileRepresentation fileRepresentation = new FileRepresentation(fileName, filePath, projectUuid);
		new FileUploadAction(fileRepresentation).execute(context, actionContext);
		verify(context).addFileRepresentation(Mockito.eq(fileRepresentation));
	}

	private FileRepresentation getAddedFile() {
		final ArgumentCaptor<FileRepresentation> captor = ArgumentCaptor.forClass(FileRepresentation.class);
		verify(context).addFileRepresentation(captor.capture());

		final FileRepresentation representation = captor.getValue();
		return representation;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new FileUploadAction(fileId, fileName, filePath);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return FileUploadAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return FileUploadActionEntity.class;
	}

}
