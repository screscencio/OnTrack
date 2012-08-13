package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.annotation.AnnotationRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(AnnotationRemoveActionEntity.class)
public class AnnotationRemoveAction implements AnnotationAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID annotationId;

	@Element
	private UUID subjectId;

	@Attribute
	private boolean userAction;

	protected AnnotationRemoveAction() {}

	public AnnotationRemoveAction(final UUID subjectId, final UUID annotationId) {
		this(subjectId, annotationId, true);
	}

	protected AnnotationRemoveAction(final UUID subjectId, final UUID annotationId, final boolean isUserAction) {
		this.subjectId = subjectId;
		this.annotationId = annotationId;
		this.userAction = isUserAction;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Annotation annotation = ActionHelper.findAnnotation(subjectId, annotationId, context);
		if (userAction && !annotation.getAuthor().getEmail().equals(actionContext.getUserEmail())) throw new UnableToCompleteActionException(
				"Can't remove a anotation created by another user.");

		final List<ModelAction> rollbackSubActions = removeSubAnnotations(context, actionContext);

		context.removeAnnotation(subjectId, annotation);
		return new AnnotationCreateAction(subjectId, annotation, rollbackSubActions);
	}

	private List<ModelAction> removeSubAnnotations(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();

		final List<Annotation> subAnnotations = context.findAnnotationsFor(annotationId);
		for (final Annotation subAnnotation : subAnnotations) {
			rollbackActions.add(new AnnotationRemoveAction(annotationId, subAnnotation.getId(), false).execute(context, actionContext));
		}
		return rollbackActions;
	}

	public UUID getAnnotationId() {
		return annotationId;
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

}
