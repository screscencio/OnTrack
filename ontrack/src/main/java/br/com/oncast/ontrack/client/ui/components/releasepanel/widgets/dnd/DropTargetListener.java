package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import com.google.gwt.user.client.ui.VerticalPanel;

public interface DropTargetListener {

	public void onDropTargetCreated(VerticalPanel dropTarget);

	public void onDropTargetRemoved(VerticalPanel dropTarget);
}
