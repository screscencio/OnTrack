package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_AT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_F2;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_PERCENT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SHARP;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.BindReleaseInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareEffortInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareProgressInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertChildInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertFatherInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertSiblingDownInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertSiblingUpInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.NodeEditionInternalAction;
import br.com.oncast.ontrack.client.utils.RuntimeEnvironment;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

// TODO Refactor this class into a shortcut manager with better resposability division and better performance while mapping interactions.
enum ScopeTreeShortcutMappings {

	UPDATE(KEY_F2, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new NodeEditionInternalAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_DOWN(KEY_ENTER, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new InsertSiblingDownInternalAction(scope));
		}
	},

	INSERT_SIBLING_SCOPE_UP(KEY_ENTER, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new InsertSiblingUpInternalAction(scope));
		}
	},

	INSERT_SCOPE_AS_CHILD(KEY_ENTER, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new InsertChildInternalAction(scope));
		}
	},

	INSERT_SCOPE_AS_PARENT(KEY_ENTER, true, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new InsertFatherInternalAction(scope));
		}
	},

	MOVE_SCOPE_UP(KEY_UP, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			actionRequestHandler.onUserActionExecutionRequest(new ScopeMoveUpAction(scope.getId()));
		}
	},

	MOVE_SCOPE_DOWN(KEY_DOWN, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			actionRequestHandler.onUserActionExecutionRequest(new ScopeMoveDownAction(scope.getId()));
		}
	},

	MOVE_SCOPE_RIGHT(KEY_RIGHT, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			actionRequestHandler.onUserActionExecutionRequest(new ScopeMoveRightAction(scope.getId()));
		}
	},

	MOVE_SCOPE_LEFT(KEY_LEFT, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			actionRequestHandler.onUserActionExecutionRequest(new ScopeMoveLeftAction(scope.getId()));
		}
	},

	DELETE_SCOPE(KEY_DELETE, false, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			actionRequestHandler.onUserActionExecutionRequest(new ScopeRemoveAction(scope.getId()));
		}
	},

	BIND_RELEASE(KEY_AT, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new BindReleaseInternalAction(context, scope));
		}
	},

	BIND_PROGRESS(KEY_PERCENT, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new DeclareProgressInternalAction(context, scope));
		}
	},

	BIND_EFFORT(KEY_SHARP, false, true, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler, final InternalActionExecutionRequestHandler internalActionHandler,
				final Scope scope, final ProjectContext context) {
			internalActionHandler.handle(new DeclareEffortInternalAction(scope, context));
		}
	};

	private final int keyUpCode;
	private final boolean controlModifier;
	private final boolean shiftModifier;
	private final boolean altModifier;

	private ScopeTreeShortcutMappings(final int keyCode, final boolean controlModifier, final boolean shiftModifier, final boolean hasAltModifier) {
		this.keyUpCode = keyCode;
		this.controlModifier = controlModifier;
		this.shiftModifier = shiftModifier;
		this.altModifier = hasAltModifier;
	}

	public static void interpretKeyboardCommand(final ActionExecutionRequestHandler applicationActionHandler,
			final InternalActionExecutionRequestHandler internalActionHandler, final int keyCode, final boolean hasShiftModifier,
			final boolean hasControlModifier, final boolean hasAltModifier, final boolean hasMetaModifier, final Scope scope, final ProjectContext context) {

		for (final ScopeTreeShortcutMappings mapping : values())

			// TODO +++The code below is platform dependent, move the platform specific key configuration elsewhere.
			if (mapping.accepts(keyCode, RuntimeEnvironment.isMac() ? (hasMetaModifier && !hasControlModifier) : hasControlModifier,
					hasShiftModifier, hasAltModifier)) {
				mapping.execute(applicationActionHandler, internalActionHandler, scope, context);
			}
	}

	protected abstract void execute(final ActionExecutionRequestHandler actionRequestHandler, InternalActionExecutionRequestHandler internalActionHandler,
			final Scope scope, ProjectContext context);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier, final boolean hasAltModifier) {
		return (this.keyUpCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier && this.altModifier == hasAltModifier);
	}
}
