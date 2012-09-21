package br.com.oncast.ontrack.shared.model.action;

import java.util.Date;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentSolveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
		final User author = ActionHelper.findUser(actionContext.getUserEmail(), context);
		final Date timestamp = actionContext.getTimestamp();

		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context);
		if (annotation.isDeprecated()) throw new UnableToCompleteActionException("Unable to create an impediment when the annotation is deprecated.");
		if (!annotation.isImpeded()) throw new UnableToCompleteActionException("Unable to solve an impediment when the annotation is not a impediment.");

		annotation.setType(AnnotationType.SOLVED_IMPEDIMENT, author, timestamp);
		return new ImpedimentCreateAction(subjectId, annotationId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

}