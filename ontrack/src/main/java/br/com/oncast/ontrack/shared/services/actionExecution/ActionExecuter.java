package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionAction;
import br.com.oncast.ontrack.shared.model.action.FileAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.TagAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;

public class ActionExecuter {

	public static ActionExecutionContext executeAction(final ProjectContext context, final ActionContext actionContext, final ModelAction action) throws UnableToCompleteActionException {
		if (action instanceof ScopeAction) return new ScopeActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ReleaseAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof KanbanAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof AnnotationAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof TeamAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof FileAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ChecklistAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ImpedimentAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof DescriptionAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof TagAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);

		throw new UnableToCompleteActionException(action, ActionExecutionErrorMessageCode.NO_MAPPED_EXECUTOR, action.getClass().toString());
	}

	public static void verifyPermissions(final ModelAction action, final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		try {
			if (action instanceof TeamInviteAction && action.getReferenceId().equals(actionContext.getUserId())) return;

			final UserRepresentation author = context.findUser(actionContext.getUserId());
			if (author.isReadOnly()) throw new UnableToCompleteActionException(action, ActionExecutionErrorMessageCode.READY_ONLY_USER);
		} catch (final UserNotFoundException e) {
			throw new UnableToCompleteActionException(action, ActionExecutionErrorMessageCode.ACTION_AUTHOR_NOT_FOUND);
		}
	}
}
