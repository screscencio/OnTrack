package br.com.oncast.ontrack.client.ui.keyeventhandler;

import java.util.Arrays;
import java.util.LinkedHashSet;

import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.keyeventhandlers.ShortcutHelpPanelShortcutMappings;
import br.com.oncast.ontrack.client.utils.jquery.Event;
import br.com.oncast.ontrack.client.utils.jquery.EventHandler;
import br.com.oncast.ontrack.client.utils.jquery.JQuery;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShortcutService {

	private static PopupConfig shortcutHelpPanelConfig;
	private static LinkedHashSet<ShortcutMapping<?>> registeredShortcuts = new LinkedHashSet<ShortcutMapping<?>>();
	private static Widget shortcutHelpPanelParentWidget;

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
		if (w.isAttached()) {
			jquery.bindKeydown(handler);
			registeredShortcuts.addAll(Arrays.asList(mappings));
		}

		final HandlerRegistration attachRegistration = w.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(final AttachEvent event) {
				if (event.isAttached()) {
					jquery.bindKeydown(handler);
					registeredShortcuts.addAll(Arrays.asList(mappings));
				}
				else {
					jquery.unbindKeyDown(handler);
					registeredShortcuts.removeAll(Arrays.asList(mappings));
				}
			}
		});

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				attachRegistration.removeHandler();
				registeredShortcuts.removeAll(Arrays.asList(mappings));

				try {
					JQuery.jquery(w).unbindKeyDown(handler);
				}
				catch (final Exception e) {}
			}
		};
	}

	public static void toggleShortcutHelpPanel() {
		if (shortcutHelpPanelConfig != null) {
			return;
		}
		else {
			shortcutHelpPanelConfig = PopupConfig.configPopup()
					.popup(new ShortcutHelpPanel(registeredShortcuts))
					.alignVertical(VerticalAlignment.TOP, new AlignmentReference(shortcutHelpPanelParentWidget, VerticalAlignment.TOP))
					.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(shortcutHelpPanelParentWidget, HorizontalAlignment.RIGHT, 10))
					.onClose(new PopupCloseListener() {
						@Override
						public void onHasClosed() {
							shortcutHelpPanelConfig = null;
						}
					})
					.setAnimationDuration(500);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					shortcutHelpPanelConfig.pop();
				}
			});
		}
	}

	public static HandlerRegistration configureShortcutHelpPanel(final Widget parentWidget) {
		shortcutHelpPanelParentWidget = parentWidget;
		return register(RootPanel.get(), null, ShortcutHelpPanelShortcutMappings.values());
	}

	public static Widget setShortcutHelpPanelParentWidget(final Widget parentWidget) {
		final Widget previous = shortcutHelpPanelParentWidget;
		shortcutHelpPanelParentWidget = parentWidget;
		return previous;
	}
}
