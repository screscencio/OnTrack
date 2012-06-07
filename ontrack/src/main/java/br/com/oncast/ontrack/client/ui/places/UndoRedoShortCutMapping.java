package br.com.oncast.ontrack.client.ui.places;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;

public enum UndoRedoShortCutMapping implements ShortcutMapping<ActionExecutionService> {
	UNDO(new Shortcut(KEY_Z).with(ControlModifier.PRESSED)) {
		@Override
		public void execute(final ActionExecutionService service) {
			service.onUserActionUndoRequest();
		}

	},
	REDO(new Shortcut(KEY_Z).with(ControlModifier.PRESSED).with(ShiftModifier.PRESSED)) {
		@Override
		public void execute(final ActionExecutionService service) {
			service.onUserActionRedoRequest();
		}
	};

	private final Shortcut shortcut;

	private UndoRedoShortCutMapping(final Shortcut shortcut) {
		this.shortcut = shortcut;
	}

	@Override
	public Shortcut getShortcut() {
		return shortcut;
	}

	@Override
	public EventProcessor getEventPostExecutionProcessor() {
		return EventProcessor.CONSUME;
	}

}