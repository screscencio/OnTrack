package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemSelectionEvent extends GwtEvent<ScopeTreeItemSelectionEventHandler> {

	public static Type<ScopeTreeItemSelectionEventHandler> TYPE;
	private final ScopeTreeItemWidget scopeWidget;

	public ScopeTreeItemSelectionEvent(final ScopeTreeItemWidget scopeWidget) {
		this.scopeWidget = scopeWidget;
	}

	public static Type<ScopeTreeItemSelectionEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeTreeItemSelectionEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeTreeItemSelectionEventHandler> getAssociatedType() {
		return ScopeTreeItemSelectionEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemSelectionEventHandler handler) {
		handler.onScopeTreeItemSelectionRequest(this);
	}

	public ScopeTreeItemWidget getScopeWidget() {
		return scopeWidget;
	}
}
