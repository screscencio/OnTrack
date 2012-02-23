package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalModelWidgetContainer<T, E extends ModelWidget<T>> extends Composite {

	private static VerticalModelWidgetContainerUiBinder uiBinder = GWT.create(VerticalModelWidgetContainerUiBinder.class);

	@SuppressWarnings("rawtypes")
	interface VerticalModelWidgetContainerUiBinder extends UiBinder<Widget, VerticalModelWidgetContainer> {}

	@UiField
	VerticalPanel verticalContainer;

	private final Map<T, E> widgetMap;

	private final ModelWidgetFactory<T, E> modelWidgetFactory;

	private final ModelWidgetContainerListener listener;

	public VerticalModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		this.modelWidgetFactory = modelWidgetFactory;
		this.listener = listener;
		widgetMap = new HashMap<T, E>();

		initWidget(uiBinder.createAndBindUi(this));
	}

	public boolean update(final List<T> modelBeanList) {
		boolean hasChanged = false;

		for (int i = 0; i < modelBeanList.size(); i++) {
			final T modelBean = modelBeanList.get(i);

			final E modelWidget = widgetMap.get(modelBean);
			if (modelWidget == null) {
				createChildModelWidgetAt(modelBean, i);
				hasChanged = true;
				continue;
			}

			if (verticalContainer.getWidgetIndex(modelWidget) != i) {
				verticalContainer.remove(modelWidget);
				verticalContainer.insert(modelWidget, i);
				hasChanged = true;
			}

			hasChanged |= modelWidget.update();
		}
		for (int i = modelBeanList.size(); i < verticalContainer.getWidgetCount(); i++) {
			@SuppressWarnings("unchecked") final E modelWidget = (E) verticalContainer.getWidget(i);
			verticalContainer.remove(i);
			widgetMap.remove(modelWidget.getModelObject());
			hasChanged = true;
		}

		listener.onUpdateComplete(hasChanged);
		return hasChanged;
	}

	public E createChildModelWidget(final T modelBean) {
		return createChildModelWidgetAt(modelBean, verticalContainer.getWidgetCount());
	}

	public E createChildModelWidgetAt(final T modelBean, final int index) {
		final E modelWidget = modelWidgetFactory.createWidget(modelBean);
		verticalContainer.insert(modelWidget, index);
		widgetMap.put(modelBean, modelWidget);

		return modelWidget;
	}

	/**
	 * Inserts a widget into internal widget map. This have to be done when some external agent, e.g., drag and drop,
	 * inserts a new child widget for this panel directly at DOM.
	 */
	public void addToWidgetMapping(final T modelBean, final E widget) {
		widgetMap.put(modelBean, widget);
	}

	public int getWidgetCount() {
		return verticalContainer.getWidgetCount();
	}

	public VerticalPanel getVerticalContainer() {
		return verticalContainer;
	}

	public E getWidgetFor(final T modelBean) {
		return widgetMap.get(modelBean);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof VerticalModelWidgetContainer)) return false;

		return widgetMap.equals(((VerticalModelWidgetContainer<?, ?>) obj).widgetMap);
	}

	public void clear() {
		verticalContainer.clear();
		widgetMap.clear();
	}
}
