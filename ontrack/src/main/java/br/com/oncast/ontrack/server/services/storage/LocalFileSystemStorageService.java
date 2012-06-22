package br.com.oncast.ontrack.server.services.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

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

import com.google.common.io.Files;

public class LocalFileSystemStorageService implements StorageService {

	private static final Logger LOGGER = Logger.getLogger(LocalFileSystemStorageService.class);

	private File baseDirectory;

	private final AuthenticationManager authenticationService;

	private final AuthorizationManager authorizationManager;

	private final PersistenceService persistenceService;

	private final BusinessLogic businessLogic;

	public LocalFileSystemStorageService(final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager,
			final PersistenceService persistenceService, final BusinessLogic businessLogic) {
		this.authenticationService = authenticationManager;
		this.authorizationManager = authorizationManager;
		this.persistenceService = persistenceService;
		this.businessLogic = businessLogic;
	}

	@Override
	public FileRepresentation store(final UUID projectId, final File file) throws IOException, PersistenceException, UnableToHandleActionException,
			AuthorizationException {
		LOGGER.debug("Persisting file '" + file.getName() + "'");
		final String fileHash = getContentHash(file);

		final File destinationFile = new File(baseDirectory, fileHash);
		if (!destinationFile.exists()) {
			Files.copy(file, destinationFile);
		}

		final FileRepresentation fileRepresentation = createFileRepresentation(projectId, file.getName(), destinationFile.getAbsolutePath());
		businessLogic.onFileUploadCompleted(fileRepresentation);
		LOGGER.debug("The file '" + fileHash + "'was persisted successfully.");
		return fileRepresentation;
	}

	private FileRepresentation createFileRepresentation(final UUID projectId, final String fileName, final String filePath) {
		return new FileRepresentation(fileName, filePath, projectId);
	}

	@Override
	public File retrieve(final UUID fileId) throws FileNotFoundException, PersistenceException, AuthorizationException {
		if (!authenticationService.isUserAuthenticated()) throw new AuthenticationException("This service requires the user to be authenticated");
		LOGGER.debug("Retrieving file '" + fileId + "'");

		try {
			final FileRepresentation fileRepresentation = persistenceService.retrieveFileRepresentationById(fileId);
			final UUID projectId = fileRepresentation.getProjectId();
			// FIXME change projectId to UUID;
			final Long longProjectId = Long.valueOf(projectId.toStringRepresentation());
			authorizationManager.assureProjectAccessAuthorization(longProjectId);

			LOGGER.debug("The file '" + fileId + "' retrieved successfully.");
			return createFile(fileRepresentation);
		}
		catch (final NoResultFoundException e) {
			throw new FileNotFoundException("The requested file was not found or the current user has no authorization to access it");
		}
	}

	private File createFile(final FileRepresentation representation) {
		final File actualFile = new File(representation.getFilePath());
		final File file = new File(Files.createTempDir(), representation.getFileName());
		try {
			Files.copy(actualFile, file);
		}
		catch (final IOException e) {
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

	private String getContentHash(final File file) throws IOException {
		try {
			final byte[] digest = Files.getDigest(file, MessageDigest.getInstance("SHA-1"));
			final String sha1Hash = new BigInteger(1, digest).toString();
			return sha1Hash;
		}
		catch (final NoSuchAlgorithmException e) {
			LOGGER.error("File persistence failed.", e);
			return file.getName();
		}
	}
}
