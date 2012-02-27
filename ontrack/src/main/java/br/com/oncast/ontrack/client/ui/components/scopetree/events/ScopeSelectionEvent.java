package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeSelectionEvent extends GwtEvent<ScopeSelectionEventHandler> {

	public static Type<ScopeSelectionEventHandler> TYPE;
	private final Scope scope;

	public ScopeSelectionEvent(final Scope scope) {
		this.scope = scope;
	}

	public static Type<ScopeSelectionEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeSelectionEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeSelectionEventHandler> getAssociatedType() {
		return ScopeSelectionEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeSelectionEventHandler handler) {
		if (getSource() != null && handler.mustIgnoreFromSource(getSource())) return;
		handler.onScopeSelectionRequest(scope);
	}

}
