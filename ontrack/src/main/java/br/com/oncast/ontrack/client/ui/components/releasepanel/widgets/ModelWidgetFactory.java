package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;


public interface ModelWidgetFactory<T, E extends ModelWidget<T>> {
	E createWidget(T modelBean);
}
