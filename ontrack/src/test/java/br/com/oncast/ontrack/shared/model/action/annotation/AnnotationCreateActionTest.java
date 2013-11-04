package br.com.oncast.ontrack.shared.model.action.annotation;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLImporter;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLWriter;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.FileRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotationCreateActionTest extends ModelActionTest {

	private UserRepresentation author;
	private UUID subjectId;
	private String message;
	private FileRepresentation attachmentFile;

	@Before
	public void setUp() throws Exception {
		subjectId = new UUID();
		author = UserRepresentationTestUtils.createUser();
		attachmentFile = FileRepresentationTestUtils.create();
		message = "Any message";

		when(actionContext.getUserId()).thenReturn(author.getId());
		when(context.findUser(author.getId())).thenReturn(author);
	}

	@Test
	public void shouldAssociateTheAnnotationWithTheAnnotatedObjectsUUID() throws Exception {
		subjectId = new UUID();
		executeAction();

		verify(context).addAnnotation(Mockito.eq(subjectId), Mockito.any(Annotation.class));
	}

	@Test
	public void shouldAssociateTheAnnotationWithTheUser() throws Exception {
		executeAction();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.any(UUID.class), captor.capture());

		assertEquals(author, captor.getValue().getAuthor());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenTheSpecifiedUserDoesNotExist() throws Exception {
		when(context.findUser(author.getId())).thenThrow(new UserNotFoundException(""));
		executeAction();
	}

	@Test
	public void shouldHaveTheMessage() throws Exception {
		executeAction();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.any(UUID.class), captor.capture());

		assertEquals(message, captor.getValue().getMessage());
	}

	@Test
	public void shouldRemoveTheCreatedAnnotationOnUndo() throws Exception {
		final ModelAction undoAction = executeAction();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.any(UUID.class), captor.capture());
		final Annotation createdAnnotation = captor.getValue();

		when(context.findAnnotation(subjectId, createdAnnotation.getId())).thenReturn(createdAnnotation);

		undoAction.execute(context, actionContext);

		verify(context).removeAnnotation(subjectId, createdAnnotation);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotCompleteWhenMessageIsEmptyAndThereIsNoAttachedFile() throws Exception {
		new AnnotationCreateAction(subjectId, AnnotationType.SIMPLE, "", null).execute(context, actionContext);
	}

	@Test
	public void shouldHaveTheAttachmentFileWhenPresent() throws Exception {
		when(context.findFileRepresentation(attachmentFile.getId())).thenReturn(attachmentFile);
		executeAction();

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.any(UUID.class), captor.capture());

		assertEquals(attachmentFile, captor.getValue().getAttachmentFile());
	}

	@Test
	public void shouldBeAbleToNotHaveAAttachmentFile() throws Exception {
		new AnnotationCreateAction(subjectId, AnnotationType.SIMPLE, message, null).execute(context, actionContext);

		final ArgumentCaptor<Annotation> captor = ArgumentCaptor.forClass(Annotation.class);
		verify(context).addAnnotation(Mockito.any(UUID.class), captor.capture());

		assertNull(captor.getValue().getAttachmentFile());
	}

	@Test
	public void xmlSerializationAndDeseiralizationShouldSupportNewLinesOnAnnotationText() throws Exception {
		message = "asd\nasd\nasd";
		final List<ProjectXMLNode> projects = createProjectXMLNodeList(UserActionTestUtils.createAnnotationCreateAction(message));

		final File file = writeXML(projects);

		final PersistenceService persistenceService = mock(PersistenceService.class);
		readXml(file, persistenceService);

		final List<ModelAction> capturedActions = getPersistedActions(persistenceService);

		assertEquals(1, capturedActions.size());
		final ModelAction action = capturedActions.get(0);

		assertTrue(action instanceof AnnotationCreateAction);
		assertEquals(message, ReflectionTestUtils.<String> get(action, "message"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ModelAction> getPersistedActions(final PersistenceService persistenceService) throws PersistenceException {
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(persistenceService).persistActions(Mockito.any(UUID.class), captor.capture(), Mockito.any(UUID.class), Mockito.any(Date.class));
		final List<ModelAction> capturedActions = captor.getValue();
		return capturedActions;
	}

	private void readXml(final File file, final PersistenceService persistenceService) throws NoResultFoundException, PersistenceException, Exception {
		when(persistenceService.retrieveUserById(UserTestUtils.getAdmin().getId())).thenReturn(UserTestUtils.getAdmin());
		final BusinessLogic businessLogic = mock(BusinessLogic.class);
		final XMLImporter importer = new XMLImporter(persistenceService, businessLogic);
		importer.loadXML(file).persistObjects();
	}

	private File writeXML(final List<ProjectXMLNode> projects) throws FileNotFoundException, IOException {
		final XMLWriter writer = setupWriter(projects);

		final File tempDir = Files.createTempDir();
		final File file = new File(tempDir, "file.xml");
		file.deleteOnExit();

		final FileOutputStream out = new FileOutputStream(file);
		writer.export(out);

		out.flush();
		out.close();
		return file;
	}

	private XMLWriter setupWriter(final List<ProjectXMLNode> projects) {
		final XMLWriter writer = new XMLWriter();
		writer.setProjectsList(projects);
		writer.setVersion("z");
		writer.setProjectAuthorizationsList(new ArrayList<ProjectAuthorizationXMLNode>());
		writer.setUserList(new ArrayList<UserXMLNode>());
		return writer;
	}

	private List<ProjectXMLNode> createProjectXMLNodeList(final UserAction userAction) throws Exception {
		final List<ProjectXMLNode> projects = new ArrayList<ProjectXMLNode>();
		final List<UserAction> actions = new ArrayList<UserAction>();
		actions.add(userAction);
		projects.add(new ProjectXMLNode(ProjectTestUtils.createRepresentation(), actions));
		return projects;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return AnnotationCreateActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return AnnotationCreateAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new AnnotationCreateAction(subjectId, AnnotationType.SIMPLE, message, attachmentFile.getId());
	}

}
