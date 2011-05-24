package br.com.oncast.ontrack.client.ui.component.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_INSERT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeActionManager;
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
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeMoveUpAction(scope));
		}
	},

	MOVE_SCOPE_DOWN(KEY_DOWN, true, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeMoveDownAction(scope));
		}
	},

	MOVE_SCOPE_RIGHT(KEY_RIGHT, true, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeMoveRightAction(scope));
		}
	},

	MOVE_SCOPE_LEFT(KEY_LEFT, true, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeMoveLeftAction(scope));
		}
	},

	UNDO(KEY_Z, true, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.undo();
		}
	},

	REDO(KEY_Y, true, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.redo();
		}
	},

	DELETE_SCOPE(KEY_DELETE, false, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeRemoveAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_DOWN(KEY_ENTER, false, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeInsertSiblingDownAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_UP(KEY_ENTER, false, true) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeInsertSiblingUpAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD(KEY_INSERT, false, false) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeInsertChildAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT(KEY_INSERT, false, true) {
		@Override
		protected void execute(final ScopeTreeActionManager actionManager, final Scope scope) {
			actionManager.execute(new ScopeInsertAsFatherAction(scope));
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
			final ScopeTreeActionManager actionManager, final Scope scope) {
		for (final ScopeTreeShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier)) mapping.execute(actionManager, scope);
	}

	protected abstract void execute(final ScopeTreeActionManager actionManager, final Scope scope);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier) {
		return (this.keyCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier);
	}
}
