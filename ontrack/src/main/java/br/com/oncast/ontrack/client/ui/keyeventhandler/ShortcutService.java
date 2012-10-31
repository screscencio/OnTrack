package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.utils.jquery.Event;
import br.com.oncast.ontrack.client.utils.jquery.EventHandler;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShortcutService {

	private ShortcutService() {}

	/**
	 * Register the given shortcut mappings to RoopPanel's instance.
	 * @param widget that is registering the shortcuts, the shortcuts are registered when this widget is attached and unregistered when it is unattached.
	 * @param parameter that the shortcut mapping will receive when the shortcut is triggered.
	 * @param mappings of the shortcuts.
	 * @return the re
	 */
	public static <T> HandlerRegistration register(final IsWidget widget, final T shortcutParameter, final ShortcutMapping<T>[] mappings) {
		final EventHandler handler = new EventHandler() {
			@Override
			public void handle(final Event e) {
				for (final ShortcutMapping<T> mapping : mappings) {
					if (mapping.getShortcut().accepts(e)) {
						mapping.getEventPostExecutionProcessor().process(e);
						mapping.execute(shortcutParameter);
					}
				}
			}
		};
		final Widget w = widget.asWidget();
		final JQuery jquery = JQuery.jquery(RootPanel.get());
		if (w.isAttached()) jquery.bindKeydown(handler);

		final HandlerRegistration attachRegistration = w.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(final AttachEvent event) {
				if (event.isAttached()) {
					jquery.bindKeydown(handler);
				}
				else {
					jquery.unbindKeyDown(handler);
				}
			}
		});

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				attachRegistration.removeHandler();

				try {
					JQuery.jquery(w).unbindKeyDown(handler);
				}
				catch (final Exception e) {}
			}
		};
	}
}
