package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.Widget;

public class MaskPanel {
	private static BasicMaskPanel maskPanel;

	private MaskPanel() {}

	public static void show(final HideHandler hideHandler, final boolean isModal) {
		getPhysicalMaskWidget().setModal(isModal).show(hideHandler);
	}

	public static void assureHidden() {
		getPhysicalMaskWidget().hide();
	}

	private static BasicMaskPanel getPhysicalMaskWidget() {
		return maskPanel == null ? maskPanel = new BasicMaskPanel() : maskPanel;
	}

	public static BasicMaskPanel get() {
		return getPhysicalMaskWidget();
	}

	public static void add(final Widget widget) {
		getPhysicalMaskWidget().add(widget);
	}
}
