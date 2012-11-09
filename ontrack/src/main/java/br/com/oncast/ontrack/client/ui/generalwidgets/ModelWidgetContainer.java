package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ModelWidgetContainer<T, E extends ModelWidget<T>> extends Composite {

	private static ModelWidgetContainerUiBinder uiBinder = GWT.create(ModelWidgetContainerUiBinder.class);

	@SuppressWarnings("rawtypes")
	interface ModelWidgetContainerUiBinder extends UiBinder<Widget, ModelWidgetContainer> {}

	@UiField(provided = true)
	protected AnimatedCellContainer<?> cellContainer;

	private final Map<T, E> widgetMap;

	private final ModelWidgetFactory<T, E> modelWidgetFactory;

	private final ModelWidgetContainerListener listener = new NullModelWidgetContainerListener();

	public ModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory) {
		this(modelWidgetFactory, new AnimatedVerticalContainer());
	}

	public ModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory, final AnimatedCellContainer<?> cellContainer) {
		this.modelWidgetFactory = modelWidgetFactory;
		widgetMap = new HashMap<T, E>();
		this.cellContainer = cellContainer;

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

			final int widgetIndex = cellContainer.getWidgetIndex(modelWidget);
			if (widgetIndex != i) {
				for (int j = widgetIndex - 1; j >= i; j--) {
					@SuppressWarnings("unchecked") final E missPlacedWidget = (E) cellContainer.getWidget(j);
					if (!modelBeanList.contains(missPlacedWidget.getModelObject())) cellContainer.remove(j);
				}
				cellContainer.move(modelWidget, i);
				hasChanged = true;
			}

			hasChanged |= modelWidget.update();
		}

		for (int i = cellContainer.getWidgetCount() - 1; i >= modelBeanList.size(); i--) {
			@SuppressWarnings("unchecked") final E modelWidget = (E) cellContainer.getWidget(i);
			cellContainer.remove(i);
			widgetMap.remove(modelWidget.getModelObject());
			hasChanged = true;
		}

		if (listener != null) listener.onUpdateComplete(hasChanged);
		return hasChanged;
	}

	private E createChildModelWidgetAt(final T modelBean, final int index) {
		final E modelWidget = modelWidgetFactory.createWidget(modelBean);
		cellContainer.insert(modelWidget, index);
		widgetMap.put(modelBean, modelWidget);
		return modelWidget;
	}

	/**
	 * Inserts a widget into internal widget map. This have to be done when some external agent, e.g., drag and drop,
	 * inserts a new child widget for this panel directly at DOM.
	 */
	public void addToWidgetMapping(final E widget) {
		widgetMap.put(widget.getModelObject(), widget);
		cellContainer.addToWidgetMapping(widget);
	}

	/**
	 * Removes a model from internal widget map. This have to be done when some external agent, e.g., drag and drop,
	 * removes a child widget for this panel directly from DOM.
	 */
	public void removeFromWidgetMapping(final T modelBean) {
		final E widget = widgetMap.remove(modelBean);
		cellContainer.removeFromWidgetMapping(widget);
	}

	public int getWidgetCount() {
		return cellContainer.getWidgetCount();
	}

	public CellPanel getCallPanel() {
		return cellContainer.getCellPanel();
	}

	public E getWidgetFor(final T modelBean) {
		return widgetMap.get(modelBean);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ModelWidgetContainer)) return false;

		return widgetMap.equals(((ModelWidgetContainer<?, ?>) obj).widgetMap);
	}

	public void clear() {
		cellContainer.clear();
		widgetMap.clear();
	}

	@SuppressWarnings("unchecked")
	public E getWidget(final int i) {
		return (E) cellContainer.getWidget(i);
	}

	private class NullModelWidgetContainerListener implements ModelWidgetContainerListener {

		@Override
		public void onUpdateComplete(final boolean hasChanged) {}
	}
}
