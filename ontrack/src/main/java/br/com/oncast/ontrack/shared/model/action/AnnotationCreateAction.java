package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(AnnotationCreateActionEntity.class)
public class AnnotationCreateAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element(required = false)
	private String message;

	@Element
	private UUID annotationId;

	@Element
	private UUID subjectId;

	@Element(required = false)
	private UUID attachmentId;

	@ElementList
	private List<ModelAction> subActionList;

	@Attribute(required = false)
	private String annotationType;

	protected AnnotationCreateAction() {}

	public AnnotationCreateAction(final UUID subjectId, final AnnotationType type, final String message, final UUID attachmentId) {
		this.message = message;
		this.attachmentId = attachmentId;
		this.annotationId = new UUID();
		this.subjectId = subjectId;
		this.annotationType = type.name();
		this.subActionList = new ArrayList<ModelAction>();
	}

	protected AnnotationCreateAction(final UUID subjectId, final Annotation annotation, final List<ModelAction> subActionList) {
		this(subjectId, annotation.getType(), annotation.getMessage(), annotation.getAttachmentFile() == null ? null : annotation.getAttachmentFile().getId());
		this.subActionList = subActionList;
		this.annotationId = annotation.getId();
	}

	public AnnotationCreateAction(final UUID subjectId, final AnnotationType type, final String message) {
		this.message = message;
		this.attachmentId = null;
		this.annotationId = new UUID();
		this.subjectId = subjectId;
		this.annotationType = type == null ? null : type.name();
		this.subActionList = new ArrayList<ModelAction>();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if ((message == null || message.isEmpty()) && attachmentId == null)
			throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ANNOTATION_WITH_EMPTY_MESSAGE_AND_ATTACHMENT);

		context.addAnnotation(subjectId, getAnnotation(context, actionContext));

		executeSubActions(context, actionContext);

		return new AnnotationRemoveAction(subjectId, annotationId);
	}

	private Annotation getAnnotation(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final UserRepresentation author = ActionHelper.findUser(actionContext.getUserId(), context, this);
		final Annotation annotation = new Annotation(annotationId, author, actionContext.getTimestamp(), message, annotationType == null ? AnnotationType.SIMPLE
				: AnnotationType.valueOf(annotationType));
		if (attachmentId != null) {
			final FileRepresentation file = ActionHelper.findFileRepresentation(attachmentId, context, this);
			annotation.setAttachmentFile(file);
		}
		return annotation;
	}

	private void executeSubActions(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		for (final ModelAction subAction : subActionList) {
			subAction.execute(context, actionContext);
		}
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}

	public AnnotationType getAnnotationType() {
		return AnnotationType.valueOf(annotationType);
	}

}
