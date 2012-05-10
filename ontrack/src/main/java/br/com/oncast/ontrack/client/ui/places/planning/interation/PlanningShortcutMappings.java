package br.com.oncast.ontrack.client.ui.places.planning.interation;

import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

public enum PlanningShortcutMappings implements ShortcutMapping<PlanningActivity> {
	SEARCH_SCOPE(new Shortcut(BrowserKeyCodes.KEY_F).with(ControlModifier.PRESSED)) {
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
	public Shortcut getShortcut() {
		return this.shortcut;
	}

	@Override
	public EventProcessor getEventPostExecutionProcessor() {
		return EventProcessor.CONSUME;
	}

}
