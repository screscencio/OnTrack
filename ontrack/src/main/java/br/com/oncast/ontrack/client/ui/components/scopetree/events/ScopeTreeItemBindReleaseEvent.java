package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemBindReleaseEvent extends GwtEvent<ScopeTreeItemBindReleaseEventHandler> {

	public static Type<ScopeTreeItemBindReleaseEventHandler> TYPE;
	private final UUID scopeId;
	private final String releaseDescription;

	public ScopeTreeItemBindReleaseEvent(final UUID scopeId, final String releaseDescription) {
		this.scopeId = scopeId;
		this.releaseDescription = releaseDescription;
	}

	public static Type<ScopeTreeItemBindReleaseEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemBindReleaseEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemBindReleaseEventHandler> getAssociatedType() {
		return ScopeTreeItemBindReleaseEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemBindReleaseEventHandler handler) {
		handler.onBindReleaseRequest(scopeId, releaseDescription);
	}
}
