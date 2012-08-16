package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeDetailChangeEvent extends GwtEvent<ScopeDetailChangeEventHandler> {

	public static Type<ScopeDetailChangeEventHandler> TYPE;
	private final Scope scope;
	private final boolean details;

	public ScopeDetailChangeEvent(final Scope scope, final boolean details) {
		this.scope = scope;
		this.details = details;
	}

	public static Type<ScopeDetailChangeEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeDetailChangeEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeDetailChangeEventHandler> getAssociatedType() {
		return ScopeDetailChangeEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeDetailChangeEventHandler handler) {
		handler.onScopeDetailChange(this);
	}

	public Scope getTargetScope() {
		return scope;
	}

	public boolean hasDetails() {
		return details;
	}

}
