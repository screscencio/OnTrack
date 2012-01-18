package br.com.oncast.ontrack.client.ui.places.planning.interation;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_F;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;

public enum PlanningShortcutMappings {

	UNDO(KEY_Z, true, false, false) {
		@Override
		protected void execute(final PlanningActivity activity) {
			activity.getActionRequestHandler().onUserActionUndoRequest();
		}
	},

	REDO(KEY_Y, true, false, false) {
		@Override
		protected void execute(final PlanningActivity activity) {
			activity.getActionRequestHandler().onUserActionRedoRequest();
		}
	},
	SEARCH_SCOPE(KEY_F, true, false, false) {
		@Override
		protected void execute(final PlanningActivity activity) {
			activity.showSearchScope();
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

	public static void interpretKeyboardCommand(final PlanningActivity activity,
			final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier,
			final boolean hasAltModifier) {
		for (final PlanningShortcutMappings mapping : values())
			if (mapping.accepts(keyCode, hasControlModifier, hasShiftModifier, hasAltModifier)) mapping.execute(activity);
	}

	protected abstract void execute(final PlanningActivity activity);

	private boolean accepts(final int keyCode, final boolean hasControlModifier, final boolean hasShiftModifier, final boolean hasAltModifier) {
		return (this.keyUpCode == keyCode && this.controlModifier == hasControlModifier && this.shiftModifier == hasShiftModifier && this.altModifier == hasAltModifier);
	}
}
