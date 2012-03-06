package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpPanel {

	private static FlowPanel focusPanel;

	public static void add(final Widget widgetToPopup) {
		getInstance().add(widgetToPopup);
	}

	private static FlowPanel getInstance() {
		return focusPanel == null ? focusPanel = createFocusPanel() : focusPanel;
	}

	private static FlowPanel createFocusPanel() {
		final FlowPanel panel = new FlowPanel();
		panel.setStyleName("popupPanel");
		return addToRootPanel(panel);
	}

	private static FlowPanel addToRootPanel(final FlowPanel panel) {
		RootPanel.get().add(panel);
		return panel;
	}

	public static void clear() {
		getInstance().clear();
	}
}
