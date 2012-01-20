package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.utils.jquery.EventHandler;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

public class ShortcutRegistration {
	private final IsWidget widget;
	private final EventHandler handler;
	private final HandlerRegistration attachRegistration;

	public ShortcutRegistration(final IsWidget widget, final EventHandler handler, final HandlerRegistration attachRegistration) {
		this.widget = widget;
		this.handler = handler;
		this.attachRegistration = attachRegistration;
	}

	public void unregister() {
		attachRegistration.removeHandler();
		JQuery.jquery(widget.asWidget()).unbindKeyDown(handler);
	}
}
