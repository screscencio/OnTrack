package br.com.oncast.ontrack.server.business;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveAnnotatedSubjectIdsException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveAnnotationsListException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationBusinessLogic {

	Set<UUID> retrieveAnnotatedSubjectIds(UUID projectId) throws UnableToRetrieveAnnotatedSubjectIdsException;

	List<Annotation> retrieveAnnotationsListFor(UUID projectId, UUID subjectId) throws UnableToRetrieveAnnotationsListException;

}
