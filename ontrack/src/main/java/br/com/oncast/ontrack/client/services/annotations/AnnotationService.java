package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface AnnotationService {

	void showAnnotationsFor(Scope scope, PopupCloseListener closeListener);

	void createAnnotationFor(UUID subjectId, String message, UUID attachmentId);

	void toggleVote(UUID annotationId, UUID subjectId);

	void showAnnotationsFor(Release release);

}
