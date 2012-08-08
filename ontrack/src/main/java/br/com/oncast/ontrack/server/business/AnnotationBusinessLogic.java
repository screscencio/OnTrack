package br.com.oncast.ontrack.server.business;

import java.util.Set;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationBusinessLogic {

	Set<UUID> retrieveAnnotatedSubjectIds(UUID projectId) throws PersistenceException;

}
