package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpPanel {

	private static FocusPanel focusPanel;

	public static void add(final Widget widgetToPopup) {
		getInstance().add(widgetToPopup);
	}

	private static FocusPanel getInstance() {
		return focusPanel == null ? focusPanel = createFocusPanel() : focusPanel;
	}

	private static FocusPanel createFocusPanel() {
		final FocusPanel panel = new FocusPanel();
		panel.setStyleName("popupPanel");
		return configureToCleanOnAnyNavigationEvent(addToRootPanel(panel));
	}

	private static FocusPanel addToRootPanel(final FocusPanel panel) {
		RootPanel.get().add(panel);
		return panel;
	}

	private static FocusPanel configureToCleanOnAnyNavigationEvent(final FocusPanel panel) {
		panel.addHandler(new PlaceChangeRequestEvent.Handler() {
			@Override
			public void onPlaceChangeRequest(final PlaceChangeRequestEvent event) {
				panel.clear();
			}
		}, PlaceChangeRequestEvent.TYPE);
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(final ValueChangeEvent<String> event) {
				panel.clear();
			}
		});
		return panel;
	}
}
