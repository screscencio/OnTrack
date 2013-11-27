package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentSolveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.Date;

import org.simpleframework.xml.Element;

@ConvertTo(ImpedimentSolveActionEntity.class)
public class ImpedimentSolveAction implements ImpedimentAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID annotationId;

	protected ImpedimentSolveAction() {}

	public ImpedimentSolveAction(final UUID subjectId, final UUID annotationId) {
		this.subjectId = subjectId;
		this.annotationId = annotationId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context, this);
		final Date timestamp = actionContext.getTimestamp();

		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context, this);
		if (annotation.isDeprecated()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.OPERATION_OVER_DEPRECATED_ANNOTATION);
		if (!annotation.isImpeded()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.SOLVE_NOT_IMPEDED_ANNOTATION);

		annotation.setType(AnnotationType.SOLVED_IMPEDIMENT, author, timestamp);
		return new ImpedimentCreateAction(subjectId, annotationId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}
}
