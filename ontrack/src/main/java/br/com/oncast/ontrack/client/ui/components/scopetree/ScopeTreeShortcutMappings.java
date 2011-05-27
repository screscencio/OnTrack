package br.com.oncast.ontrack.client.ui.components.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_INSERT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_TAB;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertAsFatherAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeRemoveAction;

// TODO Refactor this class into a shortcut manager with better resposability division and better performance while mapping interactions.
enum ScopeTreeShortcutMappings {

	MOVE_SCOPE_UP(KEY_UP, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveUpAction(scope));
		}
	},

	MOVE_SCOPE_DOWN(KEY_DOWN, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveDownAction(scope));
		}
	},

	MOVE_SCOPE_RIGHT(KEY_RIGHT, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveRightAction(scope));
		}
	},

	MOVE_SCOPE_LEFT(KEY_LEFT, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeMoveLeftAction(scope));
		}
	},

	UNDO(KEY_Z, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionUndoRequest();
		}
	},

	REDO(KEY_Y, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionRedoRequest();
		}
	},

	DELETE_SCOPE(KEY_DELETE, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeRemoveAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_DOWN(KEY_ENTER, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertSiblingDownAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_UP(KEY_ENTER, false, true) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertSiblingUpAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD(KEY_INSERT, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT(KEY_INSERT, false, true) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertAsFatherAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD_USING_TAB(KEY_TAB, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertChildAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT_USING_TAB(KEY_TAB, false, true) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
			actionRequestHandler.onActionExecutionRequest(new ScopeInsertAsFatherAction(scope));
		}
	};

	private final int keyCode;
	private final boolean controlModifier;
	private final boolean shiftModifier;

	private ScopeTreeShortcutMappings(final int keyCode, final boolean controlModifier, final boolean shiftModifier) {
		this.keyCode = keyCode;
		this.controlModifier = controlModifier;
		this.shiftModifier = shiftModifier;
	}

	public static void interpretKeyboardCommand(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier,
			final ActionExecutionRequestHandler actionRequestHandler, final Scope scope) {
		for (final ScopeTreeShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier)) mapping.execute(actionRequestHandler, scope);
	}

	protected abstract void execute(final ActionExecutionRequestHandler actionRequestHandler, final Scope scope);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier) {
		return (this.keyCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier);
	}
}
