package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.EmptyStackException;
import java.util.Stack;

public class ActionExecutionManager {

	private final Stack<ActionExecutionContext> undoStack;
	private final Stack<ActionExecutionContext> redoStack;
	private final ActionExecutionListener executionListener;
	private final ContextProviderService contextProvider;

	public ActionExecutionManager(final ContextProviderService contextProvider, final ActionExecutionListener executionListener) {
		this.contextProvider = contextProvider;
		this.executionListener = executionListener;
		undoStack = new Stack<ActionExecutionContext>();
		redoStack = new Stack<ActionExecutionContext>();
	}

	public void doNonUserAction(final UserAction action) throws UnableToCompleteActionException {
		final ProjectContext context = contextProvider.getCurrent();
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, action);
		executionListener.onActionExecution(executionContext, context, false);
	}

	public void doUserAction(final UserAction action) throws UnableToCompleteActionException {
		undoStack.push(executeUserAction(action));
		redoStack.clear();
	}

	public void undoUserAction() throws UnableToCompleteActionException {
		popExecuteAndPush(undoStack, redoStack);
	}

	public void redoUserAction() throws UnableToCompleteActionException {
		popExecuteAndPush(redoStack, undoStack);
	}

	public void undo(final ActionExecutionContext executionContext) throws UnableToCompleteActionException {
		undoStack.remove(executionContext);
		redoStack.push(revert(executionContext));
	}

	private void popExecuteAndPush(final Stack<ActionExecutionContext> from, final Stack<ActionExecutionContext> to) throws UnableToCompleteActionException {
		try {
			to.push(revert(from.pop()));
		} catch (final UnableToCompleteActionException e) {
			from.clear();
			throw e;
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	private ActionExecutionContext revert(final ActionExecutionContext executionContext) throws UnableToCompleteActionException {
		return executeUserAction(executionContext.getReverseUserAction());
	}

	private ActionExecutionContext executeUserAction(final UserAction action) throws UnableToCompleteActionException {
		final ProjectContext context = contextProvider.getCurrent();
		ActionExecuter.verifyPermissions(action, context);
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, action);
		executionListener.onActionExecution(executionContext, context, true);
		return executionContext;
	}

	public void cleanActionExecutionHistory() {
		undoStack.clear();
		redoStack.clear();
	}
}
