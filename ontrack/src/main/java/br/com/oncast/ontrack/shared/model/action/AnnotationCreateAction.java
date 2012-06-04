package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationCreateActionEntity.class)
public class AnnotationCreateAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID annotatedObjectId;

	@Attribute
	private String message;

	protected AnnotationCreateAction() {}

	public AnnotationCreateAction(final UUID annotatedObjectId, final String message) {
		this.message = message;
		this.annotationId = new UUID();
		this.annotatedObjectId = annotatedObjectId;
	}

	public AnnotationCreateAction(final Annotation annotation, final UUID annotatedObjectId) {
		this(annotatedObjectId, annotation.getMessage());
		annotationId = annotation.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final User author = ActionHelper.findUser(actionContext.getUserEmail(), context);
		final Annotation annotation = new Annotation(annotationId, author, actionContext.getTimestamp(), message);

		context.addAnnotation(annotation, annotatedObjectId);
		return new AnnotationRemoveAction(annotationId, annotatedObjectId);
	}

	@Override
	public UUID getReferenceId() {
		return annotatedObjectId;
	}

}
