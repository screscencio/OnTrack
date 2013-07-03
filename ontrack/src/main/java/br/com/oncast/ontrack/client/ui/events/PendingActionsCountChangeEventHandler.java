package br.com.oncast.ontrack.client.ui.events;

import com.google.gwt.event.shared.EventHandler;

public interface PendingActionsCountChangeEventHandler extends EventHandler {

	void onPendingActionsCountChange(PendingActionsCountChangeEvent pendingActionsCountChangeEvent);

}
