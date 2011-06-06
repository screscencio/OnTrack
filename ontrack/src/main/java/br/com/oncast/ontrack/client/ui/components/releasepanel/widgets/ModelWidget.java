package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import com.google.gwt.user.client.ui.IsWidget;

public interface ModelWidget<T> extends IsWidget {

	void update();

	T getModelObject();
}
