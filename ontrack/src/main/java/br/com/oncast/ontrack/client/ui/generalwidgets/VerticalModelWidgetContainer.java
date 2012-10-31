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

public class VerticalModelWidgetContainer<T, E extends ModelWidget<T>> extends Composite {

	private static VerticalModelWidgetContainerUiBinder uiBinder = GWT.create(VerticalModelWidgetContainerUiBinder.class);

	@SuppressWarnings("rawtypes")
	interface VerticalModelWidgetContainerUiBinder extends UiBinder<Widget, VerticalModelWidgetContainer> {}

	@UiField(provided = true)
	protected AnimatedVerticalContainer verticalContainer;

	private final Map<T, E> widgetMap;

	private final ModelWidgetFactory<T, E> modelWidgetFactory;

	private ModelWidgetContainerListener listener = new NullModelWidgetContainerListener();

	public VerticalModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory) {
		this(modelWidgetFactory, new AnimatedVerticalContainer());
	}

	public VerticalModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory, final AnimatedVerticalContainer verticalContainer) {
		this.modelWidgetFactory = modelWidgetFactory;
		this.verticalContainer = verticalContainer;
		widgetMap = new HashMap<T, E>();

		initWidget(uiBinder.createAndBindUi(this));
	}

	public VerticalModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		this(modelWidgetFactory);
		this.listener = listener;
	}

	public VerticalModelWidgetContainer(final ModelWidgetFactory<T, E> modelWidgetFactory, final AnimatedVerticalContainer verticalContainer,
			final ModelWidgetContainerListener listener) {
		this(modelWidgetFactory, verticalContainer);
		this.listener = listener;
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

			final int widgetIndex = verticalContainer.getWidgetIndex(modelWidget);
			if (widgetIndex != i) {
				for (int j = widgetIndex - 1; j >= i; j--) {
					@SuppressWarnings("unchecked") final E missPlacedWidget = (E) verticalContainer.getWidget(j);
					if (!modelBeanList.contains(missPlacedWidget.getModelObject())) verticalContainer.remove(j);
				}
				verticalContainer.move(modelWidget, i);
				hasChanged = true;
			}

			hasChanged |= modelWidget.update();
		}

		for (int i = verticalContainer.getWidgetCount() - 1; i >= modelBeanList.size(); i--) {
			@SuppressWarnings("unchecked") final E modelWidget = (E) verticalContainer.getWidget(i);
			verticalContainer.remove(i);
			widgetMap.remove(modelWidget.getModelObject());
			hasChanged = true;
		}

		if (listener != null) listener.onUpdateComplete(hasChanged);
		return hasChanged;
	}

	private E createChildModelWidgetAt(final T modelBean, final int index) {
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

	public CellPanel getCallPanel() {
		return verticalContainer.getCellPanel();
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

	@SuppressWarnings("unchecked")
	public E getWidget(final int i) {
		return (E) verticalContainer.getWidget(i);
	}

	private class NullModelWidgetContainerListener implements ModelWidgetContainerListener {

		@Override
		public void onUpdateComplete(final boolean hasChanged) {}
	}
}
