package br.com.oncast.ontrack.client.ui.generalwidgets;

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
		return addToRootPanel(panel);
	}

	private static FocusPanel addToRootPanel(final FocusPanel panel) {
		RootPanel.get().add(panel);
		return panel;
	}

	public static void clear() {
		getInstance().clear();
	}
}
