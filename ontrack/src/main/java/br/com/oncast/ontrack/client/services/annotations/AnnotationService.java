package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationService {

	void createAnnotationFor(UUID subjectId, String message, UUID attachmentId);

	void toggleVote(UUID subjectId, UUID annotationId);

	void deleteAnnotation(UUID subjectId, UUID annotationId);

	boolean hasDetails(UUID subjectId);

	void showAnnotationsFor(UUID subjectId);

}
