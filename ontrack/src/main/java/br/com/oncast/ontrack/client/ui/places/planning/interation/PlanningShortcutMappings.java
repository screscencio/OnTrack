package br.com.oncast.ontrack.client.ui.places.planning.interation;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SLASH;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventPostExecutionProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;

public enum PlanningShortcutMappings implements ShortcutMapping<PlanningActivity> {
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
	public Shortcut getShortcut() {
		return this.shortcut;
	}

	@Override
	public EventPostExecutionProcessor getEventPostExecutionProcessor() {
		return EventPostExecutionProcessor.CONSUME;
	}

}
