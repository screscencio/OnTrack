package br.com.oncast.ontrack.client.ui.generalwidgets;


public interface ModelWidgetFactory<T, E extends ModelWidget<T>> {
	E createWidget(T modelBean);
}
