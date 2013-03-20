package br.com.oncast.ontrack.client.ui.components.appmenu;

import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;

public enum ApplicationMenuShortcutMapping implements ShortcutMapping<ApplicationMenu> {
	OPEN_PROJECTS(new Shortcut(BrowserKeyCodes.KEY_P).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openProjectsMenuItem();
		}

		@Override
		public String getDescription() {
			return messages.openProjectsMenu();
		}
	},
	OPEN_NOTIFICATIONS(new Shortcut(BrowserKeyCodes.KEY_N).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openNotificationsMenuItem();
		}

		@Override
		public String getDescription() {
			return messages.openNotificationsMenu();
		}
	},
	OPEN_USER(new Shortcut(BrowserKeyCodes.KEY_U).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final ApplicationMenu menu) {
			menu.openUserMenuItem();
		}

		@Override
		public String getDescription() {
			return messages.openUserMenu();
		}
	};

	private final Shortcut shortcut;

	private static final ApplicationMenuShortcutMappingMessages messages = GWT.create(ApplicationMenuShortcutMappingMessages.class);

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
