package br.com.oncast.ontrack.client.ui.places.planning.interation;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;

public enum PlanningShortcutMappings {

	UNDO(KEY_Z, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler) {
			actionRequestHandler.onUserActionUndoRequest();
		}
	},

	REDO(KEY_Y, true, false, false) {
		@Override
		protected void execute(final ActionExecutionRequestHandler actionRequestHandler) {
			actionRequestHandler.onUserActionRedoRequest();
		}
	};

	private final int keyUpCode;
	private final boolean controlModifier;
	private final boolean shiftModifier;
	private final boolean altModifier;

	private PlanningShortcutMappings(final int keyCode, final boolean controlModifier, final boolean shiftModifier, final boolean hasAltModifier) {
		this.keyUpCode = keyCode;
		this.controlModifier = controlModifier;
		this.shiftModifier = shiftModifier;
		this.altModifier = hasAltModifier;
	}

	public static void interpretKeyboardCommand(final ActionExecutionRequestHandler applicationActionHandler,
			final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier,
			final boolean hasAltModifier) {
		for (final PlanningShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier, hasAltModifier)) mapping.execute(applicationActionHandler);
	}

	protected abstract void execute(final ActionExecutionRequestHandler actionRequestHandler);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier, final boolean hasAltModifier) {
		return (this.keyUpCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier && this.altModifier == hasAltModifier);
	}
}
