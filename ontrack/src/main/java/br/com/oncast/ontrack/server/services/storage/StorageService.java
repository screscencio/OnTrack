package br.com.oncast.ontrack.server.services.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface StorageService {

	FileRepresentation store(UUID fileId, UUID projectId, File file) throws IOException, PersistenceException, UnableToHandleActionException,
			AuthorizationException;

	File retrieve(UUID fileId) throws FileNotFoundException, PersistenceException, AuthorizationException;

	StorageService setBaseDirectory(File testDirectory);

}
