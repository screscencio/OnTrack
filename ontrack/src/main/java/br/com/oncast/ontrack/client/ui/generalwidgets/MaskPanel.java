package br.com.oncast.ontrack.client.ui.generalwidgets;

public class MaskPanel {
	private static BasicMaskPanel maskPanel;

	private MaskPanel() {}

	public static void show(final HideHandler hideHandler) {
		getPhysicalMaskWidget().show(hideHandler);
	}

	public static void assureHidden() {
		getPhysicalMaskWidget().hide();
	}

	private static BasicMaskPanel getPhysicalMaskWidget() {
		return maskPanel == null ? maskPanel = new BasicMaskPanel() : maskPanel;
	}
}
