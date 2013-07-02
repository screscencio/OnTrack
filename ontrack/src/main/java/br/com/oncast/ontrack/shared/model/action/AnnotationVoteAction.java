package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationVoteActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationVoteActionEntity.class)
public class AnnotationVoteAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID annotatedObjectId;

	protected AnnotationVoteAction() {}

	public AnnotationVoteAction(final UUID annotationId, final UUID annotatedObjectId) {
		this.annotationId = annotationId;
		this.annotatedObjectId = annotatedObjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(annotatedObjectId, annotationId, context, this);
		if (annotation.isDeprecated()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.OPERATION_OVER_DEPRECATED_ANNOTATION);

		annotation.vote(ActionHelper.findUser(actionContext.getUserId(), context, this));
		return new AnnotationVoteRemoveAction(annotationId, annotatedObjectId);
	}

	@Override
	public UUID getReferenceId() {
		return annotatedObjectId;
	}

}
