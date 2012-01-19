package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style;

public class ErrorMaskPanel {
	private static BasicMaskPanel maskPanel;

	private ErrorMaskPanel() {}

	public static void show(final HideHandler hideHandler) {
		getPhysicalMaskWidget().setFocus(true);
		getPhysicalMaskWidget().show(hideHandler);
	}

	public static void assureHidden() {
		getPhysicalMaskWidget().hide();
	}

	private static BasicMaskPanel getPhysicalMaskWidget() {
		if (maskPanel != null) return maskPanel;

		maskPanel = new BasicMaskPanel();
		final Style style = maskPanel.getStyle();
		style.setOpacity(0.4);
		style.setBackgroundColor("black");

		return maskPanel;
	}
}
