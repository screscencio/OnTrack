package br.com.oncast.ontrack.shared.model.action.helper;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.exceptions.MetadataNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

public class ActionHelper {

	// TODO ++++ remove the admin_id duplication
	private static final UUID ADMIN_ID = new UUID("admin@ontrack.com");

	public static List<ModelAction> executeSubActions(final List<ModelAction> subActions, final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final List<ModelAction> rollbackSubActions = new ArrayList<ModelAction>();
		for (final ModelAction action : subActions) {
			rollbackSubActions.add(0, action.execute(context, actionContext));
		}
		return rollbackSubActions;
	}

	public static Release findRelease(final UUID releaseId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findRelease(releaseId);
		} catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Release findRelease(final String releaseDescription, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findRelease(releaseDescription);
		} catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Scope findScope(final UUID referenceId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findScope(referenceId);
		} catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Annotation findAnnotation(final UUID subjectId, final UUID annotationId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findAnnotation(subjectId, annotationId);
		} catch (final AnnotationNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Description findDescription(final UUID subjectId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findDescriptionFor(subjectId);
		} catch (final DescriptionNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static FileRepresentation findFileRepresentation(final UUID attachmentId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findFileRepresentation(attachmentId);
		} catch (final FileRepresentationNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Checklist findChecklist(final UUID subjectId, final UUID checklistId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findChecklist(subjectId, checklistId);
		} catch (final ChecklistNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static UserRepresentation findUserFrom(final ActionContext actionContext, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		return findUser(actionContext.getUserId(), context, action);
	}

	public static UserRepresentation findUser(final UUID userId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findUser(userId);
		} catch (final UserNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static <T extends Metadata> T findMetadata(final HasMetadata subject, final MetadataType metadataType, final UUID metadataId, final ProjectContext context, final ModelAction action)
			throws UnableToCompleteActionException {
		try {
			return context.findMetadata(subject, metadataType, metadataId);
		} catch (final MetadataNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static Tag findTag(final UUID tagId, final ProjectContext context, final ModelAction action) throws UnableToCompleteActionException {
		try {
			return context.findTag(tagId);
		} catch (final TagNotFoundException e) {
			throw new UnableToCompleteActionException(action, e);
		}
	}

	public static boolean shouldIgnorePermissionVerification(final ProjectContext context, final ActionContext actionContext) {
		return context.getUsers().isEmpty() || ADMIN_ID.equals(actionContext.getUserId());
	}

}
