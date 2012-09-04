package br.com.oncast.ontrack.shared.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.FileAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ActionExecuter {

	public static ActionExecutionContext executeAction(final ProjectContext context, final ActionContext actionContext, final ModelAction action)
			throws UnableToCompleteActionException {
		if (action instanceof ScopeAction) return new ScopeActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ReleaseAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof KanbanAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof AnnotationAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof TeamAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof FileAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ChecklistAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);
		if (action instanceof ImpedimentAction) return new SimpleActionExecuter().executeAction(context, actionContext, action);

		throw new UnableToCompleteActionException("There is no mapped action executer for " + action.getClass() + ".");
	}

}
