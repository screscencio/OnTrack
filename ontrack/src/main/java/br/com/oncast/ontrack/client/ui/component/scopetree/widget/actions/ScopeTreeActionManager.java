package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import java.util.EmptyStackException;
import java.util.Stack;

import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeTreeActionManager {

	private final ScopeTreeActionFactory widgetActionFactory;
	private final Stack<ScopeAction> undoStack;
	private final Stack<ScopeAction> redoStack;

	public ScopeTreeActionManager(final ScopeTreeActionFactory widgetActionFactory) {
		this.widgetActionFactory = widgetActionFactory;
		undoStack = new Stack<ScopeAction>();
		redoStack = new Stack<ScopeAction>();
	}

	public void execute(final ScopeAction action) {
		try {
			action.execute();
			try {
				widgetActionFactory.createEquivalentActionFor(action).execute();
				undoStack.push(action);
				redoStack.clear();
			}
			catch (final ScopeNotFoundException e) {
				action.rollback();
				throw new UnableToCompleteActionException("It was not possible to complete the action due to an inconsistence between the model and the view.",
						e);
			}
		}
		catch (final UnableToCompleteActionException e) {
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
	}

	public void undo() {
		try {
			final ScopeAction action = undoStack.pop();
			action.rollback();
			try {
				widgetActionFactory.createEquivalentActionFor(action).rollback();
				redoStack.push(action);
			}
			catch (final ScopeNotFoundException e) {
				action.execute();
				throw new UnableToCompleteActionException(
						"It was not possible to complete the undo action due to an inconsistence between the model and the view.", e);
			}
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

	public void redo() {
		try {
			final ScopeAction action = redoStack.pop();
			action.execute();
			try {
				widgetActionFactory.createEquivalentActionFor(action).execute();
				undoStack.push(action);
			}
			catch (final ScopeNotFoundException e) {
				action.rollback();
				throw new UnableToCompleteActionException(
						"It was not possible to complete the redo action due to an inconsistence between the model and the view.", e);
			}
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
