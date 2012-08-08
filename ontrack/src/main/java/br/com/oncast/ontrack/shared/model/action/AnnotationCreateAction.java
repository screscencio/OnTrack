package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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

	protected AnnotationCreateAction() {}

	public AnnotationCreateAction(final UUID subjectId, final String message, final UUID attachmentId) {
		this.message = message;
		this.attachmentId = attachmentId;
		this.annotationId = new UUID();
		this.subjectId = subjectId;
		this.subActionList = new ArrayList<ModelAction>();
	}

	protected AnnotationCreateAction(final UUID subjectId, final Annotation annotation, final List<ModelAction> subActionList) {
		this(subjectId, annotation.getMessage(), annotation.getAttachmentFile() == null ? null : annotation.getAttachmentFile().getId());
		this.subActionList = subActionList;
		this.annotationId = annotation.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		if ((message == null || message.isEmpty()) && attachmentId == null) throw new UnableToCompleteActionException(
				"A annotation should have a message or an attachment file");

		context.addAnnotation(subjectId, getAnnotation(context, actionContext));

		executeSubActions(context, actionContext);

		return new AnnotationRemoveAction(subjectId, annotationId);
	}

	public Annotation getAnnotation(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final User author = ActionHelper.findUser(actionContext.getUserEmail(), context);
		final Annotation annotation = new Annotation(annotationId, author, actionContext.getTimestamp(), message);
		if (attachmentId != null) {
			final FileRepresentation file = ActionHelper.findFileRepresentation(attachmentId, context);
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

}
