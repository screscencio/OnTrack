package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveDeprecationActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(AnnotationRemoveDeprecationActionEntity.class)
public class AnnotationRemoveDeprecationAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID annotationId;

	public AnnotationRemoveDeprecationAction() {}

	public AnnotationRemoveDeprecationAction(final UUID subjectId, final UUID annotationId) {
		this.subjectId = subjectId;
		this.annotationId = annotationId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context, this);
		annotation.setDeprecation(DeprecationState.VALID, ActionHelper.findUser(actionContext.getUserId(), context, this), actionContext.getTimestamp());
		return new AnnotationDeprecateAction(subjectId, annotationId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(final UUID annotationId) {
		this.annotationId = annotationId;
	}

}
