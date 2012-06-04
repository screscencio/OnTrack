package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationRemoveActionEntity.class)
public class AnnotationRemoveAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID annotatedObjectId;

	public AnnotationRemoveAction() {}

	public AnnotationRemoveAction(final UUID id, final UUID annotatedObjectId) {
		this.annotationId = id;
		this.annotatedObjectId = annotatedObjectId;

	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(annotationId, annotatedObjectId, context);
		context.removeAnnotation(annotation, annotatedObjectId);
		return new AnnotationCreateAction(annotation, annotatedObjectId);
	}

	@Override
	public UUID getReferenceId() {
		return annotationId;
	}

}
