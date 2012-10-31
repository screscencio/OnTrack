package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

public enum ApplicationMenuShortcutMapping implements ShortcutMapping<ApplicationMenu> {
	OPEN_PROJECTS(new Shortcut(BrowserKeyCodes.KEY_P).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openProjectsMenuItem();
		}
	},
	OPEN_NOTIFICATIONS(new Shortcut(BrowserKeyCodes.KEY_N).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openNotificationsMenuItem();
		}
	},
	OPEN_MEMBERS(new Shortcut(BrowserKeyCodes.KEY_M).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openMembersMenuItem();
		}
	},
	OPEN_USER(new Shortcut(BrowserKeyCodes.KEY_U).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openUserMenuItem();
		}
	};

	private final Shortcut shortcut;

	private ApplicationMenuShortcutMapping(final Shortcut shortcut) {
		this.shortcut = shortcut;
	}

	@Override
	public Shortcut getShortcut() {
		return this.shortcut;
	}

	@Override
	public EventProcessor getEventPostExecutionProcessor() {
		return EventProcessor.CONSUME;
	}

}
