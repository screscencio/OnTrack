package br.com.oncast.ontrack.client.services.actionExecution;

import java.util.EmptyStackException;
import java.util.Stack;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

public class ActionExecutionManager {

	private final Stack<ModelAction> undoStack;
	private final Stack<ModelAction> redoStack;
	private final ActionExecutionListener executionListener;

	public ActionExecutionManager(final ActionExecutionListener executionListener) {
		this.executionListener = executionListener;
		undoStack = new Stack<ModelAction>();
		redoStack = new Stack<ModelAction>();
	}

	public void doNonUserAction(final ModelAction action, final ProjectContext context)
			throws UnableToCompleteActionException {
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, action);
		executionListener.onActionExecution(action, context, executionContext.getInferenceInfluencedScopeSet(), false);
	}

	public void doUserAction(final ModelAction action, final ProjectContext context)
			throws UnableToCompleteActionException {
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, action);
		executionListener.onActionExecution(action, context, executionContext.getInferenceInfluencedScopeSet(), true);
		final ModelAction undoAction = executionContext.getReverseAction();
		undoStack.push(undoAction);
		redoStack.clear();
	}

	public void undoUserAction(final ProjectContext context) throws UnableToCompleteActionException {
		try {
			final ModelAction undoAction = undoStack.pop();
			final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, undoAction);
			executionListener.onActionExecution(undoAction, context, executionContext.getInferenceInfluencedScopeSet(), true);
			final ModelAction redoAction = executionContext.getReverseAction();
			redoStack.push(redoAction);
		}
		catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			throw e;
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	public void redoUserAction(final ProjectContext context) throws UnableToCompleteActionException {
		try {
			final ModelAction redoAction = redoStack.pop();
			final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, redoAction);
			executionListener.onActionExecution(redoAction, context, executionContext.getInferenceInfluencedScopeSet(), true);
			final ModelAction undoAction = executionContext.getReverseAction();
			undoStack.push(undoAction);
		}
		catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			throw e;
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}
}
