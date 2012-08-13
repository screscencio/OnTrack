package br.com.oncast.ontrack.client.services.annotations;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.annotations.AnnotationServiceImpl.AnnotationModificationListener;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnnotationService {

	void createAnnotationFor(UUID subjectId, String message, UUID attachmentId);

	void toggleVote(UUID subjectId, UUID annotationId);

	void deleteAnnotation(UUID subjectId, UUID annotationId);

	void showAnnotationsFor(UUID subjectId);

	boolean hasDetails(UUID subjectId);

	void loadAnnotationsFor(UUID subjectId, AsyncCallback<List<Annotation>> asyncCallback);

	void loadAnnotatedSubjectIds(AsyncCallback<Set<UUID>> callback);

	boolean isAnnotatedSubjectIdsAvailable();

	void addAnnotationCreationListener(AnnotationModificationListener annotationCreationListener);

	void removeAnnotationCreationListener(AnnotationModificationListener annotationCreationListener);

}
