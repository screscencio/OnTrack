package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemEditionCancelEvent extends GwtEvent<ScopeTreeItemEditionCancelEventHandler> {

	public static Type<ScopeTreeItemEditionCancelEventHandler> TYPE;

	public static Type<ScopeTreeItemEditionCancelEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemEditionCancelEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemEditionCancelEventHandler> getAssociatedType() {
		return ScopeTreeItemEditionCancelEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemEditionCancelEventHandler handler) {
		handler.onItemEditionCancel();
	}
}
