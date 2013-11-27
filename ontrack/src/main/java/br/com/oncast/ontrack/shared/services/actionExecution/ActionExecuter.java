package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;

public class ActionExecuter {

	public static ActionExecutionContext executeAction(final ProjectContext context, final UserAction action) throws UnableToCompleteActionException {
		if (action instanceof ScopeAction) return new ScopeActionExecuter().executeAction(context, action);
		else return new SimpleActionExecuter().executeAction(context, action);
	}

	public static void verifyPermissions(final UserAction action, final ProjectContext context) throws UnableToCompleteActionException {
		try {
			if (ActionHelper.shouldIgnorePermissionVerification(action.getUserId(), context)) return;

			final UserRepresentation author = context.findUser(action.getUserId());
			if (author.isReadOnly()) throw new UnableToCompleteActionException(action.getModelAction(), ActionExecutionErrorMessageCode.READY_ONLY_USER);
		} catch (final UserNotFoundException e) {
			throw new UnableToCompleteActionException(action.getModelAction(), ActionExecutionErrorMessageCode.ACTION_AUTHOR_NOT_FOUND);
		}
	}

}
