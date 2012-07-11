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
	private UUID subjectId;

	protected AnnotationRemoveAction() {}

	public AnnotationRemoveAction(final UUID subjectId, final UUID annotationId) {
		this.annotationId = annotationId;
		this.subjectId = subjectId;

	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(annotationId, subjectId, context);
		if (!annotation.getAuthor().getEmail().equals(actionContext.getUserEmail())) throw new UnableToCompleteActionException(
				"Can't remove a anotation created by another user.");
		context.removeAnnotation(annotation, subjectId);
		return new AnnotationCreateAction(annotation, subjectId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

}
