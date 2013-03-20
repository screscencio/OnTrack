package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("unchecked")
public class ModelWidgetContainerDragHandler<T> implements DragHandler {

	private ModelWidgetContainer<T, ModelWidget<T>> lastWidgetContainer;

	@Override
	public void onDragStart(final DragStartEvent event) {
		try {
			final ModelWidget<T> modelWidget = (ModelWidget<T>) event.getContext().draggable;
			lastWidgetContainer = getModelWidgetContainer(modelWidget);
			lastWidgetContainer.removeFromWidgetMapping(modelWidget.getModelObject());
		}
		catch (final ClassCastException e) {}
	}

	@Override
	public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {}

	@Override
	public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		try {
			final ModelWidget<T> widget = getDraggableModelWidget(event);
			if (hasCancelled(event)) {
				addToPreviousContainer(widget);
				return;
			}

			addToCurrentContainer(widget);
		}
		catch (final ClassCastException e) {}
	}

	private ModelWidget<T> getDraggableModelWidget(final DragEndEvent event) {
		final ModelWidget<T> widget = (ModelWidget<T>) event.getContext().draggable;
		return widget;
	}

	private void addToPreviousContainer(final ModelWidget<T> widget) {
		if (lastWidgetContainer == null) return;

		lastWidgetContainer.addToWidgetMapping(widget);
		lastWidgetContainer = null;
	}

	protected void addToCurrentContainer(final ModelWidget<T> widget) {
		getModelWidgetContainer(widget).addToWidgetMapping(widget);
	}

	private boolean hasCancelled(final DragEndEvent event) {
		return event.getContext().finalDropController == null;
	}

	private ModelWidgetContainer<T, ModelWidget<T>> getModelWidgetContainer(final ModelWidget<T> modelWidget) {
		Widget container = modelWidget.asWidget();
		while (container.getParent() != null && !(container instanceof ModelWidgetContainer)) {
			container = container.getParent();
		}

		return (ModelWidgetContainer<T, ModelWidget<T>>) container;
	}

}
