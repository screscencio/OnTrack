package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.EmptyStackException;
import java.util.Stack;

public class ActionExecutionManager {

	private final Stack<ModelAction> undoStack;
	private final Stack<ModelAction> redoStack;
	private final ActionExecutionListener executionListener;

	public ActionExecutionManager(final ActionExecutionListener executionListener) {
		this.executionListener = executionListener;
		undoStack = new Stack<ModelAction>();
		redoStack = new Stack<ModelAction>();
	}

	public void doNonUserAction(final ModelAction action, final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		executionListener.onActionExecution(action, context, actionContext, executionContext, false);
	}

	public void doUserAction(final ModelAction action, final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		undoStack.push(executeUserAction(action, context, actionContext));
		redoStack.clear();
	}

	public void undoUserAction(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		try {
			redoStack.push(executeUserAction(undoStack.pop(), context, actionContext));
		} catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			throw e;
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	public void redoUserAction(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		try {
			undoStack.push(executeUserAction(redoStack.pop(), context, actionContext));
		} catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			throw e;
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	public ModelAction executeUserAction(final ModelAction action, final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		ActionExecuter.verifyPermissions(action, context, actionContext);
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		executionListener.onActionExecution(action, context, actionContext, executionContext, true);
		return executionContext.getReverseAction();
	}

	public void cleanActionExecutionHistory() {
		undoStack.clear();
		redoStack.clear();
	}
}
