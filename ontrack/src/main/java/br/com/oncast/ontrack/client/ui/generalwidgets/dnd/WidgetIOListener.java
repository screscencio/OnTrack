package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface WidgetIOListener {

	void onWidgetInserted(IsWidget child, int index);

	void onWidgetRemoved(Widget child);

}