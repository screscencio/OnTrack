package br.com.oncast.ontrack.client.ui.places.planning.interation;

import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningActivity;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;

public enum PlanningShortcutMappings implements ShortcutMapping<PlanningActivity> {
	SEARCH_SCOPE(new Shortcut(BrowserKeyCodes.KEY_F).with(ControlModifier.PRESSED)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.showSearchScope();
		}

		@Override
		public String getDescription() {
			return messages.searchScope();
		}
	},
	EXPORT_TO_MIND_MAP(new Shortcut(BrowserKeyCodes.KEY_E).with(ShiftModifier.PRESSED).with(ControlModifier.PRESSED)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.exportToMindMap();
		}

		@Override
		public String getDescription() {
			return messages.exportToMindMap();
		}
	},
	TOGGLE_RELEASE_PANEL(new Shortcut(BrowserKeyCodes.KEY_R).with(AltModifier.PRESSED)) {
		@Override
		public void execute(final PlanningActivity activity) {
			activity.toggleReleasePanel();
		}

		@Override
		public String getDescription() {
			return messages.toggleReleasePanel();
		}
	};

	private final Shortcut shortcut;
	private static final PlanningShortcutMappingsMessage messages = GWT.create(PlanningShortcutMappingsMessage.class);

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
