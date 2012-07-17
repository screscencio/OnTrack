package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpPanel {

	static List<Widget> addedWidgets;

	public static void add(final Widget widgetToPopup) {
		widgetToPopup.getElement().getStyle().setZIndex(20);
		getAddedWidgetsList().add(widgetToPopup);
		RootPanel.get().add(widgetToPopup);
	}

	private static List<Widget> getAddedWidgetsList() {
		return addedWidgets == null ? addedWidgets = new ArrayList<Widget>() : addedWidgets;
	}

	public static void clear() {
		for (final Widget widget : getAddedWidgetsList()) {
			widget.removeFromParent();
		}
		getAddedWidgetsList().clear();
	}
}
