package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.utils.jquery.Event;

public enum EventProcessor {

	PREVENT_DEFAULT {
		@Override
		public void process(final Event e) {
			e.preventDefault();
		}
	},
	STOP_PROPAGATION {
		@Override
		public void process(final Event e) {
			e.stopPropagation();
		}
	},
	DO_NOTHING {
		@Override
		public void process(final Event e) {}
	},
	CONSUME {
		@Override
		public void process(final Event e) {
			PREVENT_DEFAULT.process(e);
			STOP_PROPAGATION.process(e);
		}
	};

	public abstract void process(Event e);

}
