package br.com.oncast.ontrack.client.ui.components.scopetree;

import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_F2;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_INSERT;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.shared.util.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.InternalActionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.InternalInsertionAction;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertAsFatherAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

// TODO Refactor this class into a shortcut manager with better resposability division and better performance while mapping interactions.
enum ScopeTreeShortcutMappings {

	UPDATE(KEY_F2, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			internalActionHandler.onEditionModeRequest();
		}
	},

	MOVE_SCOPE_UP(KEY_UP, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveUpAction(scope));
		}
	},

	MOVE_SCOPE_DOWN(KEY_DOWN, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveDownAction(scope));
		}
	},

	MOVE_SCOPE_RIGHT(KEY_RIGHT, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveRightAction(scope));
		}
	},

	MOVE_SCOPE_LEFT(KEY_LEFT, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveLeftAction(scope));
		}
	},

	UNDO(KEY_Z, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionUndoRequest();
		}
	},

	REDO(KEY_Y, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionRedoRequest();
		}
	},

	DELETE_SCOPE(KEY_DELETE, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeRemoveAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_DOWN(KEY_ENTER, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			// TODO Externalize this class below
			internalActionHandler.onInternalActionExecutionRequest(new InternalInsertionAction() {

				private ScopeTreeItem newTreeItem;

				@Override
				public void execute(final ScopeTreeItem treeItem) throws UnableToCompleteActionException {
					if (treeItem.isRoot()) throw new UnableToCompleteActionException("It is not possible to create a sibling for a root node.");
					newTreeItem = new ScopeTreeItem(new Scope(""));

					final ScopeTreeItem parentItem = treeItem.getParentItem();
					parentItem.insertItem(parentItem.getChildIndex(treeItem) + 1, newTreeItem);

					newTreeItem.getTree().setSelectedItem(newTreeItem);
					internalActionHandler.onEditionModeRequest();
				}

				@Override
				public void rollback() throws UnableToCompleteActionException {
					newTreeItem.remove();
				}

				@Override
				public ModelAction createEquivalentModelAction(final String pattern) {
					return new ScopeInsertSiblingDownAction(scope, pattern);
				}
			});
		}
	},

	INSERT_SIBLING_SCOPE_UP(KEY_ENTER, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertSiblingUpAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD(KEY_INSERT, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT(KEY_INSERT, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionRequestHandler internalActionHandler,
				final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertAsFatherAction(scope));
		}
	};

	private final int keyCode;
	private final boolean controlModifier;
	private final boolean shiftModifier;
	private final boolean altModifier;

	private ScopeTreeShortcutMappings(final int keyCode, final boolean controlModifier, final boolean shiftModifier, final boolean hasAltModifier) {
		this.keyCode = keyCode;
		this.controlModifier = controlModifier;
		this.shiftModifier = shiftModifier;
		this.altModifier = hasAltModifier;
	}

	public static void interpretKeyboardCommand(final ActionExecutionRequestHandler applicationActionHandler,
			final InternalActionRequestHandler internalActionHandler, final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier,
			final boolean hasAltModifier, final Scope scope) {
		for (final ScopeTreeShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier, hasAltModifier)) mapping.execute(applicationActionHandler,
					internalActionHandler, scope);
	}

	protected abstract void execute(final ActionExecutionRequestHandler actionRequestHandler, InternalActionRequestHandler internalActionHandler,
			final Scope scope);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier, final boolean hasAltModifier) {
		return (this.keyCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier && this.altModifier == hasAltModifier);
	}
}
