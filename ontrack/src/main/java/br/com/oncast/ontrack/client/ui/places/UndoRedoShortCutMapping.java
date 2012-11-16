package br.com.oncast.ontrack.client.ui.places;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;

import com.google.gwt.core.client.GWT;

public enum UndoRedoShortCutMapping implements ShortcutMapping<ActionExecutionService> {
	UNDO(new Shortcut(KEY_Z).with(ControlModifier.PRESSED)) {
		@Override
		public void execute(final ActionExecutionService service) {
			service.onUserActionUndoRequest();
		}

		@Override
		public String getDescription() {
			return messages.undo();
		}
	},
	REDO(new Shortcut(KEY_Z).with(ControlModifier.PRESSED).with(ShiftModifier.PRESSED)) {
		@Override
		public void execute(final ActionExecutionService service) {
			service.onUserActionRedoRequest();
		}

		@Override
		public String getDescription() {
			return messages.redo();
		}
	};

	private final Shortcut shortcut;
	private static final UndoRedoShortCutMappingMessages messages = GWT.create(UndoRedoShortCutMappingMessages.class);

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
