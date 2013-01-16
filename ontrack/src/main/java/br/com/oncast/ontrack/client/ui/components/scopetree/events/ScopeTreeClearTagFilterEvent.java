package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeClearTagFilterEvent extends GwtEvent<ScopeTreeClearTagFilterEventHandler> {

	private static Type<ScopeTreeClearTagFilterEventHandler> TYPE;

	@Override
	public GwtEvent.Type<ScopeTreeClearTagFilterEventHandler> getAssociatedType() {
		return getType();
	}

	public static GwtEvent.Type<ScopeTreeClearTagFilterEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeTreeClearTagFilterEventHandler>() : TYPE;
	}

	@Override
	protected void dispatch(final ScopeTreeClearTagFilterEventHandler handler) {
		handler.onClearTagFilterRequested();
	}

}
