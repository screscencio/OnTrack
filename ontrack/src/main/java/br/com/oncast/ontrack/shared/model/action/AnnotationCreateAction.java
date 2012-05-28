package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationCreateActionEntity.class)
public class AnnotationCreateAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	private UUID annotationId;

	private UUID annotatedObjectId;

	private User author;

	protected AnnotationCreateAction() {}

	public AnnotationCreateAction(final UUID annotatedObjectId, final User author) {
		this.annotationId = new UUID();
		this.annotatedObjectId = annotatedObjectId;
		this.author = author;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		context.addAnnotation(new Annotation(annotationId, author), annotatedObjectId);
		return null;
	}

	@Override
	public UUID getReferenceId() {
		return annotationId;
	}

}
