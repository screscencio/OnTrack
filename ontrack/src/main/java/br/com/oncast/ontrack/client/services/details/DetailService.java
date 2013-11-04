package br.com.oncast.ontrack.client.services.details;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.SubjectDetailUpdateEvent;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

public interface DetailService {

	void createAnnotationFor(UUID subjectId, AnnotationType selectedType, String message, UUID attachmentId);

	void deprecateAnnotation(UUID subjectId, UUID annotationId);

	void addVote(UUID subjectId, UUID annotationId);

	void removeVote(UUID subjectId, UUID annotationId);

	void showDetailsFor(UUID subjectId);

	boolean hasDetails(UUID subjectId);

	List<Annotation> getAnnotationsFor(UUID subjectId);

	void removeDeprecation(UUID subjectId, UUID annotationId);

	void markAsImpediment(UUID subjectId, UUID annotationId);

	void markAsSolveImpediment(UUID subjectId, UUID annotationId);

	boolean hasOpenImpediment(UUID subjectId);

	SubjectDetailUpdateEvent getDetailUpdateEvent(UUID subjectId);

	void updateDescription(final UUID subjectId, final String text);

	List<Annotation> getImpedimentsFor(UUID subjectId);

	void removeAnnotation(UUID subjectId, UUID annotationId);

}
