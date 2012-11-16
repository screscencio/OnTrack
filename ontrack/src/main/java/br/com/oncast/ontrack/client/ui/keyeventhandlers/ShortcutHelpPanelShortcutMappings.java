package br.com.oncast.ontrack.client.ui.keyeventhandlers;

import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;

public enum ShortcutHelpPanelShortcutMappings implements ShortcutMapping<Void> {
	SHOW_SHORTCUT_HELP_PANEL(new Shortcut(BrowserKeyCodes.KEY_SLASH).with(ShiftModifier.PRESSED)) {
		@Override
		public void execute(final Void target) {
			ShortcutService.toggleShortcutHelpPanel();
		}

		@Override
		public String getDescription() {
			return messages.showShortcutHelpPanel();
		}
	};

	private final Shortcut shortcut;
	private static final ShortcutHelpPanelShortcutMappingsMessages messages = GWT.create(ShortcutHelpPanelShortcutMappingsMessages.class);

	private ShortcutHelpPanelShortcutMappings(final Shortcut shortcut) {
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
