package br.com.oncast.ontrack.server.services.storage;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class LocalFileSystemStorageService implements StorageService {

	private static final String INFO_FILE_SUFFIX = ".info";

	private static final Logger LOGGER = Logger.getLogger(LocalFileSystemStorageService.class);

	private static final String FILE_REFERENCE_COUNTER = "reference_count";

	private File baseDirectory;

	private final AuthenticationManager authenticationService;

	private final AuthorizationManager authorizationManager;

	private final PersistenceService persistenceService;

	private final BusinessLogic businessLogic;

	private final Properties properties;

	public LocalFileSystemStorageService(final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager, final PersistenceService persistenceService,
			final BusinessLogic businessLogic) {
		this.authenticationService = authenticationManager;
		this.authorizationManager = authorizationManager;
		this.persistenceService = persistenceService;
		this.businessLogic = businessLogic;
		properties = new Properties();
	}

	@Override
	public FileRepresentation store(final UUID fileId, final UUID projectId, final File file) throws IOException, PersistenceException, UnableToHandleActionException, AuthorizationException {
		LOGGER.debug("Persisting file '" + file.getName() + "'");
		final String fileHash = generateContentHash(file);

		final File destinationFile = new File(baseDirectory, fileHash);
		if (!destinationFile.exists()) {
			Files.copy(file, destinationFile);
		}

		updateInfoFile(destinationFile);

		final FileRepresentation fileRepresentation = createFileRepresentation(fileId, projectId, file.getName(), destinationFile.getAbsolutePath());
		businessLogic.onFileUploadCompleted(fileRepresentation);
		LOGGER.debug("The file '" + fileHash + "'was persisted successfully.");
		return fileRepresentation;
	}

	private void updateInfoFile(final File destinationFile) throws IOException {
		final File infoFile = new File(destinationFile.getAbsolutePath() + INFO_FILE_SUFFIX);
		if (!infoFile.exists()) infoFile.createNewFile();

		properties.load(new FileInputStream(infoFile));
		final String counter = properties.getProperty(FILE_REFERENCE_COUNTER, "0");
		properties.setProperty(FILE_REFERENCE_COUNTER, String.valueOf(Integer.valueOf(counter) + 1));
		properties.store(new FileOutputStream(infoFile), "");
	}

	private FileRepresentation createFileRepresentation(final UUID fileId, final UUID projectId, final String fileName, final String filePath) {
		return new FileRepresentation(fileId, fileName, filePath, projectId);
	}

	@Override
	public File retrieve(final UUID fileId) throws FileNotFoundException, PersistenceException, AuthorizationException {
		if (!authenticationService.isUserAuthenticated()) throw new AuthenticationException("This service requires the user to be authenticated");
		LOGGER.debug("Retrieving file '" + fileId + "'");

		try {
			final FileRepresentation fileRepresentation = persistenceService.retrieveFileRepresentationById(fileId);
			authorizationManager.assureActiveProjectAccessAuthorization(fileRepresentation.getProjectId());

			LOGGER.debug("The file '" + fileId + "' retrieved successfully.");
			return createFile(fileRepresentation);
		} catch (final NoResultFoundException e) {
			throw new FileNotFoundException("The requested file was not found or the current user has no authorization to access it");
		}
	}

	private File createFile(final FileRepresentation representation) {
		final File actualFile = new File(representation.getFilePath());
		final File file = new File(Files.createTempDir(), representation.getFileName());
		try {
			Files.copy(actualFile, file);
		} catch (final IOException e) {
			LOGGER.error("File copy failed.", e);
		}
		return file;
	}

	@Override
	public StorageService setBaseDirectory(final File baseDirectory) {
		this.baseDirectory = baseDirectory;
		if (!baseDirectory.exists()) {
			baseDirectory.mkdirs();
		}

		return this;
	}

	private String generateContentHash(final File file) throws IOException {
		final HashCode digest = Files.hash(file, Hashing.sha1());
		return new BigInteger(1, digest.asBytes()).toString();
	}
}
