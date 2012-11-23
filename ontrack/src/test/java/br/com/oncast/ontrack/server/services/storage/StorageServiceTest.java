package br.com.oncast.ontrack.server.services.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.persistence.NoResultException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.FileRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class StorageServiceTest {

	private static final Charset CHARSET = Charsets.UTF_8;
	private static final UUID PROJECT_ID = new UUID();
	private StorageService storage;
	private String fileName;
	private File file;
	private File testDirectory;
	private String fileContent;
	private File storageBaseDirectory;
	private String otherFileContent;
	private File otherFileWithSameName;
	private UUID projectId;

	@Mock
	private AuthenticationManager authenticationService;
	@Mock
	private AuthorizationManager authenticationManager;
	@Mock
	private PersistenceService persistenceService;
	@Mock
	private BusinessLogic businessLogic;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectId = PROJECT_ID;

		testDirectory = Files.createTempDir();
		testDirectory.mkdir();
		storageBaseDirectory = createSubDirectory("storage");

		storage = getNewInstance();

		fileName = "any_file.ext";
		fileContent = "content";
		file = createFileWithContent(testDirectory, fileName, fileContent);

		otherFileContent = "other content";
		otherFileWithSameName = createFileWithContent(createSubDirectory("otherDirectory"), fileName, otherFileContent);

		when(authenticationService.isUserAuthenticated()).thenReturn(true);
	}

	@After
	public void cleanUp() {
		delete(testDirectory);
	}

	@Test
	public void shouldBeAbleToPersistAFileAndRetrieveThePersistedFile() throws Exception {
		final UUID fileId = store(file);
		assertFileIsStored(fileId, fileName, fileContent);
	}

	@Test
	public void shouldReturnAFileWithSameName() throws Exception {
		final UUID fileHash = store(file);
		assertEquals(fileName, retrieve(fileHash).getName());
	}

	@Test
	public void filesWithSameNameAndDifferentContentsShouldNotOverride() throws Exception {
		store(file);
		final UUID otherFileHash = store(otherFileWithSameName);

		final String retrievedFileContent = retrieveFileContent(otherFileHash);
		assertFalse(fileContent.equals(retrievedFileContent));
		assertEquals(otherFileContent, retrievedFileContent);
	}

	@Test
	public void erasingTheGivenFileDoesNotErasesTheStoredFile() throws Exception {
		final UUID fileHash = store(file);
		assertTrue(file.delete());
		assertFileIsStored(fileHash, fileName, fileContent);
	}

	@Test
	public void allPersistedFileReferenceShouldBeAvailableToRetrieve() throws Exception {
		final FileRepresentation persistedFileRepresentation = FileRepresentationTestUtils.create();
		final UUID id = persistedFileRepresentation.getId();
		when(persistenceService.retrieveFileRepresentationById(id)).thenReturn(persistedFileRepresentation);

		final StorageService newInstance = getNewInstance();

		assertEquals(persistedFileRepresentation.getFileName(), newInstance.retrieve(id).getName());
	}

	@Test(expected = AuthenticationException.class)
	public void notAuthenticatedUsersShouldNotBeAbleToRetrieveTheFile() throws Exception {
		final UUID fileHash = store(file);

		when(authenticationService.isUserAuthenticated()).thenReturn(false);
		retrieve(fileHash);
	}

	@Test
	public void differentUsersStoringFilesWithSameNameShouldNotOverrideThePreviousFile() throws Exception {
		store(file);

		final User otherUser = UserTestUtils.createUser();
		when(authenticationService.getAuthenticatedUser()).thenReturn(otherUser);
		final UUID fileHash = store(otherFileWithSameName);

		assertFalse(fileContent.equals(retrieveFileContent(fileHash)));
		assertEquals(otherFileContent, retrieveFileContent(fileHash));
	}

	@Test
	public void shouldNotOverrideFilesWithSameNameInDifferentProjects() throws Exception {
		final UUID fileHash = store(file);

		setCurrentProject(new UUID());
		final UUID otherFileHash = store(otherFileWithSameName);

		assertEquals(otherFileContent, retrieveFileContent(otherFileHash));

		setCurrentProject(PROJECT_ID);
		assertFileIsStored(fileHash, fileName, fileContent);
	}

	@Test(expected = FileNotFoundException.class)
	public void shouldThrowFileNotFoundExceptionWhenTheRequestedFileWasNotPersistedBefore() throws Exception {
		when(persistenceService.retrieveFileRepresentationById(Mockito.any(UUID.class))).thenThrow(new NoResultFoundException("", new NoResultException()));
		retrieve(new UUID());
	}

	@Test(expected = AuthorizationException.class)
	public void shouldThrowAuthorizationExceptionWhenTheRequestedFileWasPersistedInDifferentProject() throws Exception {
		final UUID fileHash = store(file);
		setCurrentProject(new UUID());

		retrieve(fileHash);
	}

	@Test
	public void persistedFilesShouldBeKept() throws Exception {
		final UUID fileHash = store(file);

		final String fileName2 = "file2.ext";
		final String fileContent2 = "content 2";
		final UUID fileHash2 = storeFile(fileName2, fileContent2);

		final String fileName3 = "picture.png";
		final String fileContent3 = "0xAE32A9";
		final UUID fileHash3 = storeFile(fileName3, fileContent3);

		assertFileIsStored(fileHash, fileName, fileContent);
		assertFileIsStored(fileHash2, fileName2, fileContent2);
		assertFileIsStored(fileHash3, fileName3, fileContent3);
	}

	@Test
	public void shouldNotifyBussinessLogicOfTheFileUpload() throws Exception {
		final UUID fileId = store(file);
		final ArgumentCaptor<FileRepresentation> captor = ArgumentCaptor.forClass(FileRepresentation.class);
		verify(businessLogic).onFileUploadCompleted(captor.capture());
		final FileRepresentation representation = captor.getValue();
		assertEquals(fileId, representation.getId());
		assertEquals(fileName, representation.getFileName());
		assertNotNull(representation.getFilePath());
		assertEquals(projectId, representation.getProjectId());
	}

	@Test
	public void shouldWriteAInformationFileCountingTheReferenceAssociation() throws Exception {
		final Properties properties = new Properties();
		final String contentHash = generateContentHash(file);
		final File infoFile = new File(storageBaseDirectory, contentHash
				+ ReflectionTestUtils.getStatic(LocalFileSystemStorageService.class, "INFO_FILE_SUFFIX"));
		final String fileReferenceCounter = (String) ReflectionTestUtils.getStatic(LocalFileSystemStorageService.class, "FILE_REFERENCE_COUNTER");

		for (int i = 1; i <= 10; i++) {
			store(file);
			properties.load(new FileInputStream(infoFile));
			assertEquals(new Integer(i), Integer.valueOf(properties.getProperty(fileReferenceCounter)));
		}
	}

	@Test
	public void shouldCreateAFileRepresentationWithTheGivenUUID() throws Exception {
		final UUID fileId = new UUID();
		storage.store(fileId, projectId, file);
		final ArgumentCaptor<FileRepresentation> captor = ArgumentCaptor.forClass(FileRepresentation.class);
		verify(businessLogic).onFileUploadCompleted(captor.capture());

		assertEquals(fileId, captor.getValue().getId());
	}

	private String generateContentHash(final File file) throws Exception {
		final Method method = LocalFileSystemStorageService.class.getDeclaredMethod("generateContentHash", File.class);
		method.setAccessible(true);
		final String hash = (String) method.invoke(storage, file);
		return hash;
	}

	private UUID store(final File file) throws Exception {
		final ArgumentCaptor<FileRepresentation> captor = ArgumentCaptor.forClass(FileRepresentation.class);
		doNothing().when(businessLogic).onFileUploadCompleted(captor.capture());

		final UUID fileId = new UUID();
		storage.store(fileId, projectId, file);

		when(persistenceService.retrieveFileRepresentationById(fileId)).thenReturn(captor.getValue());
		return fileId;
	}

	private File retrieve(final UUID fileId) throws IOException, PersistenceException, AuthorizationException {
		return storage.retrieve(fileId);
	}

	private void assertFileIsStored(final UUID fileId, final String fileName, final String content) throws IOException, PersistenceException,
			AuthorizationException {
		final File retrievedFile = retrieve(fileId);
		assertEquals(content, getContent(retrievedFile));
		assertEquals(fileName, retrievedFile.getName());
	}

	private UUID storeFile(final String name, final String content) throws Exception {
		return store(createFileWithContent(testDirectory, name, content));
	}

	private void setCurrentProject(final UUID projectId) throws PersistenceException, AuthorizationException {
		this.projectId = projectId;
		doThrow(new AuthorizationException()).when(authenticationManager).assureProjectAccessAuthorization(Mockito.any(UUID.class));
		doNothing().when(authenticationManager).assureProjectAccessAuthorization(projectId);
	}

	private StorageService getNewInstance() {
		return new LocalFileSystemStorageService(authenticationService, authenticationManager, persistenceService, businessLogic)
				.setBaseDirectory(storageBaseDirectory);
	}

	private String retrieveFileContent(final UUID fileId) throws IOException, PersistenceException, AuthorizationException {
		return getContent(retrieve(fileId));
	}

	private String getContent(final File expected) throws IOException {
		return Files.toString(expected, CHARSET);
	}

	private File createSubDirectory(final String string) {
		final File dir = new File(testDirectory, string);
		dir.mkdir();
		return dir;
	}

	private File createFileWithContent(final File directory, final String name, final String content) throws IOException {
		final File f = new File(directory, name);
		Files.write(content, f, CHARSET);
		return f;
	}

	private boolean delete(final File file) {
		if (file.isDirectory()) {
			final String[] children = file.list();
			for (final String element : children) {
				final boolean success = delete(new File(file, element));
				if (!success) return false;
			}
		}

		return file.delete();
	}

}
