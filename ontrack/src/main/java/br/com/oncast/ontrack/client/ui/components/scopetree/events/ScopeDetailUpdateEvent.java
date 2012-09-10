package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeDetailUpdateEvent extends GwtEvent<ScopeDetailUpdateEventHandler> {

	public static Type<ScopeDetailUpdateEventHandler> TYPE;
	private final Scope scope;
	private final boolean details;
	private final boolean hasOpenImpediments;

	public ScopeDetailUpdateEvent(final Scope scope, final boolean hasDetails, final boolean hasOpenImpediments) {
		this.scope = scope;
		this.details = hasDetails;
		this.hasOpenImpediments = hasOpenImpediments;
	}

	public static Type<ScopeDetailUpdateEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ScopeDetailUpdateEventHandler>() : TYPE;
	}

	@Override
	public Type<ScopeDetailUpdateEventHandler> getAssociatedType() {
		return ScopeDetailUpdateEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeDetailUpdateEventHandler handler) {
		handler.onScopeDetailUpdate(this);
	}

	public Scope getTargetScope() {
		return scope;
	}

	public boolean hasDetails() {
		return details;
	}

	public boolean hasOpenImpediments() {
		return hasOpenImpediments;
	}

}
