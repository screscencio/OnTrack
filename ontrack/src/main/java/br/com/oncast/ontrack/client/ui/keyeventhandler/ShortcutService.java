package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.utils.jquery.Event;
import br.com.oncast.ontrack.client.utils.jquery.EventHandler;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ShortcutService {

	private ShortcutService() {}

	public static <T> ShortcutRegistration register(final IsWidget isWidget, final T callBackParameter, final ShortcutMapping<T>[] mappings) {
		final EventHandler handler = new EventHandler() {
			@Override
			public void handle(final Event e) {
				for (final ShortcutMapping<T> mapping : mappings) {
					if (mapping.getShortcut().accepts(e)) {
						mapping.execute(callBackParameter);
						mapping.getEventPostExecutionProcessor().process(e);
					}
				}
			}
		};
		final Widget widget = isWidget.asWidget();
		final JQuery jquery = JQuery.jquery(widget);
		if (widget.isAttached()) jquery.bindKeydown(handler);

		final HandlerRegistration attachRegistration = widget.addAttachHandler(new AttachEvent.Handler() {
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
		return new ShortcutRegistration(isWidget, handler, attachRegistration);
	}
}
