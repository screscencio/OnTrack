package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import com.google.gwt.event.shared.GwtEvent;

public class ClearTagFilterEvent extends GwtEvent<ClearTagFilterEventHandler> {

	private static Type<ClearTagFilterEventHandler> TYPE;

	@Override
	public GwtEvent.Type<ClearTagFilterEventHandler> getAssociatedType() {
		return getType();
	}

	public static GwtEvent.Type<ClearTagFilterEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ClearTagFilterEventHandler>() : TYPE;
	}

	@Override
	protected void dispatch(final ClearTagFilterEventHandler handler) {
		handler.onClearTagFilterRequested();
	}

}
