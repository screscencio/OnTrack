package br.com.oncast.ontrack.client.ui.generalwidgets;

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

	// TODO review z-index strategy for maskpanel.
	private static BasicMaskPanel getPhysicalMaskWidget() {
		if (maskPanel != null) maskPanel.asWidget().removeFromParent();

		maskPanel = new BasicMaskPanel().setModal(true);
		return maskPanel;
	}
}
