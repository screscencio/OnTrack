package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class MaskPanel {
	private static FocusPanel maskPanel;
	private static HideHandler currentHideHandler;

	private MaskPanel() {}

	public static void show(final HideHandler hideHandler) {
		if (getPhysicalMaskWidget().isVisible()) throw new RuntimeException("The MaskPanel is already visible.");

		currentHideHandler = hideHandler;
		getPhysicalMaskWidget().setVisible(true);
	}

	public static void assureHidden() {
		if (currentHideHandler != null) {
			final HideHandler lastHideHandler = currentHideHandler;
			currentHideHandler = null;
			lastHideHandler.onWillHide();
		}

		getPhysicalMaskWidget().setVisible(false);
	}

	private static FocusPanel getPhysicalMaskWidget() {
		if (maskPanel != null) return maskPanel;

		maskPanel = new FocusPanel();
		maskPanel.setStyleName("maskPanel");
		maskPanel.setVisible(false);
		RootPanel.get().add(maskPanel);

		maskPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				assureHidden();
			}
		});

		return maskPanel;
	}
}
