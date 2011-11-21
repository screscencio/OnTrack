package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class MaskPanel {
	private static FocusPanel maskPanel;
	private static HandlerRegistration internalHandler;

	private MaskPanel() {}

	public static void show(final HideHandler hideHandler) {
		if (getPhysicalMaskWidget().isVisible()) throw new RuntimeException("The MaskPanel is already visible.");
		getPhysicalMaskWidget().setVisible(true);
		internalHandler = getPhysicalMaskWidget().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (hideHandler != null) hideHandler.onWillHide();
				hide();
			}
		});
	}

	private static void hide() {
		getPhysicalMaskWidget().setVisible(false);
		internalHandler.removeHandler();
		internalHandler = null;
	}

	private static FocusPanel getPhysicalMaskWidget() {
		if (maskPanel != null) return maskPanel;

		maskPanel = new FocusPanel();
		maskPanel.setStyleName("maskPanel");
		maskPanel.setVisible(false);
		RootPanel.get().add(maskPanel);
		return maskPanel;
	}
}
