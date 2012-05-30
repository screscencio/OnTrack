package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationService {

	void showAnnotationsFor(Scope scope);

	void createAnnotationFor(UUID subjectId, String message);

}
