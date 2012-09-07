package br.com.oncast.ontrack.client.services.annotations;

import java.util.List;

import br.com.oncast.ontrack.client.services.annotations.AnnotationServiceImpl.AnnotationModificationListener;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationService {

	void createAnnotationFor(UUID subjectId, String message, UUID attachmentId);

	void deprecateAnnotation(UUID subjectId, UUID annotationId);

	void addVote(UUID subjectId, UUID annotationId);

	void removeVote(UUID subjectId, UUID annotationId);

	void showAnnotationsFor(UUID subjectId);

	boolean hasDetails(UUID subjectId);

	void addAnnotationModificationListener(AnnotationModificationListener annotationCreationListener);

	void removeAnnotationModificationListener(AnnotationModificationListener annotationCreationListener);

	List<Annotation> getAnnotationsFor(UUID subjectId);

	void removeDeprecation(UUID subjectId, UUID annotationId);

	void markAsImpediment(UUID subjectId, UUID annotationId);

	void markAsSolveImpediment(UUID subjectId, UUID annotationId);

}
