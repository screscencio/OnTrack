package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import com.google.gwt.user.client.ui.VerticalPanel;

public interface ReleaseWidgetAttachListener {

	void onDetached(VerticalPanel dropTarget);

	void onAttached(VerticalPanel droppableArea);
}
