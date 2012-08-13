package br.com.oncast.ontrack.server.business;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveAnnotatedSubjectIdsException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveAnnotationsListException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationBusinessLogicImpl implements AnnotationBusinessLogic {

	private final PersistenceService persistenceService;

	private static final Logger LOGGER = Logger.getLogger(AnnotationBusinessLogicImpl.class);

	public AnnotationBusinessLogicImpl(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public Set<UUID> retrieveAnnotatedSubjectIds(final UUID projectId) throws UnableToRetrieveAnnotatedSubjectIdsException {
		try {
			return persistenceService.retrieveAnnotatedSubjectIdsFromProject(projectId);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to retrieve Annotated subject's ids. Persistence problems.";
			LOGGER.error(message, e);
			throw new UnableToRetrieveAnnotatedSubjectIdsException(message);
		}
	}

	@Override
	public List<Annotation> retrieveAnnotationsListFor(final UUID projectId, final UUID subjectId) throws UnableToRetrieveAnnotationsListException {
		try {
			return this.persistenceService.retrieveAnnotationsBySubjectId(projectId, subjectId);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to retrieve Annotations list for subject " + subjectId.toStringRepresentation() + ". Persistence problems.";
			LOGGER.error(message, e);
			throw new UnableToRetrieveAnnotationsListException(message);
		}
	}
}
