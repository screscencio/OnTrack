package br.com.oncast.ontrack.client.ui.places.planning.interation;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SLASH;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_U;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventPostExecutionProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;

public enum PlanningShortcutMappings implements ShortcutMapping<PlanningActivity> {
	UNDO(new Shortcut(KEY_U)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.getActionRequestHandler().onUserActionUndoRequest();
		}

	},
	REDO(new Shortcut(KEY_U).with(ShiftModifier.PRESSED)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.getActionRequestHandler().onUserActionRedoRequest();
		}
	},
	SEARCH_SCOPE(new Shortcut(KEY_SLASH)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.showSearchScope();
		}
	};

	private final Shortcut shortcut;

	PlanningShortcutMappings(final Shortcut shortcut) {
		this.shortcut = shortcut;
	}

	@Override
	public abstract void execute(PlanningActivity target);

	@Override
	public Shortcut getShortcut() {
		return this.shortcut;
	}

	@Override
	public EventPostExecutionProcessor getEventPostExecutionProcessor() {
		return EventPostExecutionProcessor.CONSUME;
	}

}
