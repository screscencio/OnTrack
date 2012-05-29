package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationRemoveAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final UUID annotatedObjectId;

	public AnnotationRemoveAction(final UUID id, final UUID annotatedObjectId) {
		this.id = id;
		this.annotatedObjectId = annotatedObjectId;

	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(id, context);
		context.removeAnnotation(annotation, annotatedObjectId);
		return new AnnotationCreateAction(annotation, annotatedObjectId);
	}

	@Override
	public UUID getReferenceId() {
		// FIXME Auto-generated catch block
		return null;
	}

}
