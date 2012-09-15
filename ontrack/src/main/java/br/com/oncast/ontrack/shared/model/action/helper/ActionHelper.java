package br.com.oncast.ontrack.shared.model.action.helper;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionHelper {

	public static User findUser(final String userEmail, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findUser(userEmail);
		}
		catch (final UserNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Release findRelease(final UUID referenceId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findRelease(referenceId);
		}
		catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Release findRelease(final String releaseDescription, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findRelease(releaseDescription);
		}
		catch (final ReleaseNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Scope findScope(final UUID referenceId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findScope(referenceId);
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Annotation findAnnotation(final UUID subjectId, final UUID annotationId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findAnnotation(subjectId, annotationId);
		}
		catch (final AnnotationNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static FileRepresentation findFileRepresentation(final UUID attachmentId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findFileRepresentation(attachmentId);
		}
		catch (final FileRepresentationNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static Checklist findChecklist(final UUID subjectId, final UUID checklistId, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			return context.findChecklist(subjectId, checklistId);
		}
		catch (final ChecklistNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	public static User findUserFrom(final ActionContext actionContext, final ProjectContext context) throws UnableToCompleteActionException {
		return findUser(actionContext.getUserEmail(), context);
	}

}
