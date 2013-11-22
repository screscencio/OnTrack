package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.util.Date;

import org.simpleframework.xml.Element;

@ConvertTo(ImpedimentCreateActionEntity.class)
public class ImpedimentCreateAction implements ImpedimentAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID subjectId;

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

	protected ImpedimentCreateAction() {}

	public ImpedimentCreateAction(final UUID subjectId, final UUID annotationId) {
		this.uniqueId = new UUID();
		this.subjectId = subjectId;
		this.annotationId = annotationId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context, this);
		final Date timestamp = actionContext.getTimestamp();

		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context, this);
		if (annotation.isDeprecated()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.OPERATION_OVER_DEPRECATED_ANNOTATION);

		final AnnotationType previousType = annotation.getType();
		annotation.setType(AnnotationType.OPEN_IMPEDIMENT, author, timestamp);
		return new ImpedimentRemoveAction(subjectId, annotationId, previousType);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}
}
