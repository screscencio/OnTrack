package br.com.oncast.ontrack.client.ui.events;

import com.google.gwt.event.shared.GwtEvent;

public class PendingActionsCountChangeEvent extends GwtEvent<PendingActionsCountChangeEventHandler> {

	public static Type<PendingActionsCountChangeEventHandler> TYPE;

	private final int pendingActionsCount;

	public PendingActionsCountChangeEvent(final int pendingActionsCount) {
		this.pendingActionsCount = pendingActionsCount;
	}

	public int getPendingActionsCount() {
		return pendingActionsCount;
	}

	public static Type<PendingActionsCountChangeEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<PendingActionsCountChangeEventHandler>() : TYPE;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PendingActionsCountChangeEventHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(final PendingActionsCountChangeEventHandler handler) {
		handler.onPendingActionsCountChange(this);
	}

}
