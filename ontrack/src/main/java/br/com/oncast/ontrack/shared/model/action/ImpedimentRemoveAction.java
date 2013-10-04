package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.impediments.ImpedimentRemoveActionEntity;
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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ImpedimentRemoveActionEntity.class)
public class ImpedimentRemoveAction implements ImpedimentAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID annotationId;

	@Attribute
	private String previousType;

	public ImpedimentRemoveAction() {}

	public ImpedimentRemoveAction(final UUID subjectId, final UUID annotationId, final AnnotationType previousType) {
		this.subjectId = subjectId;
		this.annotationId = annotationId;
		this.previousType = previousType.toString();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context, this);
		final Date timestamp = actionContext.getTimestamp();

		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context, this);

		if (!annotation.isImpeded()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_IMPEDIMENT_FROM_NOT_IMPEDED_ANNOTATION);
		if (!annotation.getAuthorForState(AnnotationType.OPEN_IMPEDIMENT).getId().equals(actionContext.getUserId()))
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.REMOVE_IMPEDIMENT_OF_ANOTHER_AUTHOR);

		annotation.setType(AnnotationType.valueOf(previousType), author, timestamp);
		return new ImpedimentCreateAction(subjectId, annotationId);
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

	public String getPreviousType() {
		return previousType;
	}

	public void setPreviousType(final String previousType) {
		this.previousType = previousType;
	}

}
