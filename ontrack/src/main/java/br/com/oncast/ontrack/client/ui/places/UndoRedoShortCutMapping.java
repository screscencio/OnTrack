package br.com.oncast.ontrack.client.ui.places;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_U;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventPostExecutionProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;

public enum UndoRedoShortCutMapping implements ShortcutMapping<ActionExecutionService> {
	UNDO(new Shortcut(KEY_U)) {
		@Override
		public void execute(final ActionExecutionService service) {
			service.onUserActionUndoRequest();
		}

	},
	REDO(new Shortcut(KEY_U).with(ShiftModifier.PRESSED)) {
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
	public EventPostExecutionProcessor getEventPostExecutionProcessor() {
		return EventPostExecutionProcessor.CONSUME;
	}

}
