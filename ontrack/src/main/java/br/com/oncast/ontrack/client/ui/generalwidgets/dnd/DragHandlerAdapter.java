package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;

public abstract class DragHandlerAdapter implements DragHandler {

	@Override
	public void onDragStart(final DragStartEvent event) {}

	@Override
	public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {}

	@Override
	public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {}

}
