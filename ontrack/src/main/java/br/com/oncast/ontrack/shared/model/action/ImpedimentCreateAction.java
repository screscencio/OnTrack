package br.com.oncast.ontrack.shared.model.action;

import java.util.Date;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ImpedimentCreateActionEntity.class)
public class ImpedimentCreateAction implements ImpedimentAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID subjectId;

	protected ImpedimentCreateAction() {}

	public ImpedimentCreateAction(final UUID subjectId, final UUID annotationId) {
		this.subjectId = subjectId;
		this.annotationId = annotationId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final User author = ActionHelper.findUser(actionContext.getUserEmail(), context);
		final Date timestamp = actionContext.getTimestamp();

		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context);
		if (annotation.isDeprecated()) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.OPERATION_OVER_DEPRECATED_ANNOTATION);

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
