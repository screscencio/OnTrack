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
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertChildScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertFatherScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionManager;
import br.com.oncast.ontrack.shared.beans.Scope;

// TODO Refactor this class into a shortcut manager with better resposability division and better performance while mapping interactions.
enum ScopeTreeShortcutMappings {

	MOVE_SCOPE_UP(KEY_UP, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new MoveUpScopeAction(scope));
		}
	},

	MOVE_SCOPE_DOWN(KEY_DOWN, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new MoveDownScopeAction(scope));
		}
	},

	MOVE_SCOPE_RIGHT(KEY_RIGHT, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new MoveRightScopeAction(scope));
		}
	},

	MOVE_SCOPE_LEFT(KEY_LEFT, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new MoveLeftScopeAction(scope));
		}
	},

	UNDO(KEY_Z, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.undo();
		}
	},

	REDO(KEY_Y, true, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.redo();
		}
	},

	DELETE_SCOPE(KEY_DELETE, false, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new RemoveScopeAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_DOWN(KEY_ENTER, false, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new InsertSiblingDownScopeAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_UP(KEY_ENTER, false, true) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new InsertSiblingUpScopeAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD(KEY_INSERT, false, false) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new InsertChildScopeAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT(KEY_INSERT, false, true) {
		@Override
		protected void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
			actionManager.execute(new InsertFatherScopeAction(scope));
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
			final ScopeTreeWidgetActionManager actionManager, final Scope scope) {
		for (final ScopeTreeShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier)) mapping.execute(actionManager, scope);
	}

	protected abstract void execute(final ScopeTreeWidgetActionManager actionManager, final Scope scope);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier) {
		return (this.keyCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier);
	}
}
