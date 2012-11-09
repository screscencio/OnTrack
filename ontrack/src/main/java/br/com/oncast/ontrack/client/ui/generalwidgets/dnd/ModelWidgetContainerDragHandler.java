package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("unchecked")
public abstract class ModelWidgetContainerDragHandler<T> implements DragHandler {

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
			final ModelWidget<T> widget = (ModelWidget<T>) event.getContext().draggable;
			final DropController dropTargetController = event.getContext().finalDropController;
			if (isDropTargetInvalid(dropTargetController) && lastWidgetContainer != null) {
				lastWidgetContainer.addToWidgetMapping(widget);
				lastWidgetContainer = null;
				return;
			}

			getModelWidgetContainer(widget).addToWidgetMapping(widget);
		}
		catch (final ClassCastException e) {}
	}

	private boolean isDropTargetInvalid(final DropController dropController) {
		return dropController == null;
	}

	private ModelWidgetContainer<T, ModelWidget<T>> getModelWidgetContainer(final ModelWidget<T> modelWidget) {
		Widget container = modelWidget.asWidget();
		while (container.getParent() != null && !(container instanceof ModelWidgetContainer)) {
			container = container.getParent();
		}

		return (ModelWidgetContainer<T, ModelWidget<T>>) container;
	}

}
