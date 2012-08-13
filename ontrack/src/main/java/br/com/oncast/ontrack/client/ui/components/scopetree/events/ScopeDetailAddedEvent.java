package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeDetailAddedEvent extends GwtEvent<ScopeDetailAddedEventHandler> {

	public static Type<ScopeDetailAddedEventHandler> TYPE;
	private final Scope scope;

	public ScopeDetailAddedEvent(final Scope scope) {
		this.scope = scope;
	}

	public static Type<ScopeDetailAddedEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeDetailAddedEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeDetailAddedEventHandler> getAssociatedType() {
		return ScopeDetailAddedEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeDetailAddedEventHandler handler) {
		handler.onScopeDetailAdded(this);
	}

	public Scope getTargetScope() {
		return scope;
	}

}
