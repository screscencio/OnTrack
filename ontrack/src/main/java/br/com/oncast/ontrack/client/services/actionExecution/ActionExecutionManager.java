package br.com.oncast.ontrack.client.services.actionExecution;

import java.util.EmptyStackException;
import java.util.Stack;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

public class ActionExecutionManager {

	private final ActionExecutionListener executionListener;
	private final Stack<ModelAction> undoStack;
	private final Stack<ModelAction> redoStack;

	public ActionExecutionManager(final ActionExecutionListener actionExecutionListener) {
		executionListener = actionExecutionListener;
		undoStack = new Stack<ModelAction>();
		redoStack = new Stack<ModelAction>();
	}

	// FIXME Throw UnableToCompleteActionException
	public void doExecute(final ModelAction action, final ProjectContext context) {
		try {
			final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, action);
			executionListener.onActionExecution(action, context, executionContext.getInferenceInfluencedScopeSet());
			final ModelAction undoAction = executionContext.getReverseAction();
			undoStack.push(undoAction);
			redoStack.clear();
		}
		catch (final UnableToCompleteActionException e) {
			// TODO ++Implement an adequate exception treatment.
			// TODO ++Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
	}

	// FIXME Throw UnableToCompleteActionException
	public void undo(final ProjectContext context) {
		try {
			final ModelAction undoAction = undoStack.pop();
			final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, undoAction);
			executionListener.onActionExecution(undoAction, context, executionContext.getInferenceInfluencedScopeSet());
			final ModelAction redoAction = executionContext.getReverseAction();
			redoStack.push(redoAction);
		}
		catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			// TODO ++Implement an adequate exception treatment.
			// TODO ++Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	// FIXME Throw UnableToCompleteActionException
	public void redo(final ProjectContext context) {
		try {
			final ModelAction redoAction = redoStack.pop();
			final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, redoAction);
			executionListener.onActionExecution(redoAction, context, executionContext.getInferenceInfluencedScopeSet());
			final ModelAction undoAction = executionContext.getReverseAction();
			undoStack.push(undoAction);
		}
		catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			// TODO ++Implement an adequate exception treatment.
			// TODO ++Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}
}
