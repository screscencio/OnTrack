package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

public class MaskPanel {
	private static ArrayList<BasicMaskPanel> showingMaskPanels;

	private MaskPanel() {}

	public static BasicMaskPanel show(final HideHandler hideHandler, final boolean isModal, final boolean hideOnClick) {
		final BasicMaskPanel panel = getPhysicalMaskWidget();
		getShowingMaskPanels().add(panel);
		panel.setModal(isModal).hideOnClick(hideOnClick).show(hideHandler);
		return panel;
	}

	private static List<BasicMaskPanel> getShowingMaskPanels() {
		if (showingMaskPanels == null) showingMaskPanels = new ArrayList<BasicMaskPanel>();
		return showingMaskPanels;
	}

	public static void assureHidden() {
		for (final BasicMaskPanel panel : new ArrayList<BasicMaskPanel>(getShowingMaskPanels()))
			panel.hide();

		getShowingMaskPanels().clear();
	}

	private static BasicMaskPanel getPhysicalMaskWidget() {
		return new BasicMaskPanel();
	}

}
