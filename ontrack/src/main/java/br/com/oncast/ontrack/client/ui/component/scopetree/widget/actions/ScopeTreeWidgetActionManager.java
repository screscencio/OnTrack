package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import java.util.EmptyStackException;
import java.util.Stack;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;

public class ScopeTreeWidgetActionManager {

	private final ScopeTreeWidgetActionFactory widgetActionFactory;
	private final Stack<ScopeAction> undoStack;
	private final Stack<ScopeAction> redoStack;

	public ScopeTreeWidgetActionManager(final ScopeTreeWidgetActionFactory widgetActionFactory) {
		this.widgetActionFactory = widgetActionFactory;
		undoStack = new Stack<ScopeAction>();
		redoStack = new Stack<ScopeAction>();
	}

	public void execute(final ScopeAction action) {
		try {
			action.execute();
			try {
				widgetActionFactory.getEquivalentActionFor(action).execute();
				undoStack.push(action);
				redoStack.clear();
			} catch (final UnableToCompleteActionException e) {
				action.rollback();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
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
				widgetActionFactory.getEquivalentActionFor(action).rollback();
				redoStack.push(action);
			} catch (final UnableToCompleteActionException e) {
				action.execute();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	public void redo() {
		try {
			final ScopeAction action = redoStack.pop();
			action.execute();
			try {
				widgetActionFactory.getEquivalentActionFor(action).execute();
				undoStack.push(action);
			} catch (final UnableToCompleteActionException e) {
				action.rollback();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}
}
