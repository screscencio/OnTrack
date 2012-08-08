package br.com.oncast.ontrack.server.business;

import java.util.Set;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationBusinessLogicImpl implements AnnotationBusinessLogic {

	private final PersistenceService persistenceService;

	public AnnotationBusinessLogicImpl(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public Set<UUID> retrieveAnnotatedSubjectIds(final UUID projectId) throws PersistenceException {
		return persistenceService.retrieveAnnotatedSubjectIdsFromProject(projectId);
	}

}
