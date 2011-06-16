package br.com.oncast.ontrack.client.services.actionExecution;

import java.util.EmptyStackException;
import java.util.Stack;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ActionExecutionManager {

	private final ActionExecutionListener executionListener;
	private final Stack<ScopeAction> undoStack;
	private final Stack<ScopeAction> redoStack;

	public ActionExecutionManager(final ActionExecutionListener actionExecutionListener) {
		executionListener = actionExecutionListener;
		undoStack = new Stack<ScopeAction>();
		redoStack = new Stack<ScopeAction>();
	}

	public void execute(final ScopeAction action, final ProjectContext context) {
		try {
			action.execute(context);
			executionListener.onActionExecution(action, context, false);
			undoStack.push(action);
			redoStack.clear();
		}
		catch (final UnableToCompleteActionException e) {
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
	}

	public void undo(final ProjectContext context) {
		try {
			final ScopeAction action = undoStack.pop();
			action.rollback(context);
			executionListener.onActionExecution(action, context, true);
			redoStack.push(action);
		}
		catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	public void redo(final ProjectContext context) {
		try {
			final ScopeAction action = redoStack.pop();
			action.execute(context);
			executionListener.onActionExecution(action, context, false);
			undoStack.push(action);
		}
		catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
		catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}
}
