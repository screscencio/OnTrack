package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationDeprecateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.DeprecationState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(AnnotationDeprecateActionEntity.class)
public class AnnotationDeprecateAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID annotationId;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	protected AnnotationDeprecateAction() {}

	public AnnotationDeprecateAction(final UUID subjectId, final UUID annotationId) {
		this.uniqueId = new UUID();
		this.subjectId = subjectId;
		this.annotationId = annotationId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context, this);
		annotation.setDeprecation(DeprecationState.DEPRECATED, ActionHelper.findUser(actionContext.getUserId(), context, this), actionContext.getTimestamp());
		return new AnnotationRemoveDeprecationAction(subjectId, annotationId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}
}
