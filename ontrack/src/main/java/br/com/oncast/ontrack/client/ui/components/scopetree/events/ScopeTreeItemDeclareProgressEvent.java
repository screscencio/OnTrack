package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemDeclareProgressEvent extends GwtEvent<ScopeTreeItemDeclareProgressEventHandler> {

	public static Type<ScopeTreeItemDeclareProgressEventHandler> TYPE;
	private final UUID scopeId;
	private final String progressDescription;

	public ScopeTreeItemDeclareProgressEvent(final UUID scopeId, final String progressDescription) {
		this.scopeId = scopeId;
		this.progressDescription = progressDescription;
	}

	public static Type<ScopeTreeItemDeclareProgressEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemDeclareProgressEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemDeclareProgressEventHandler> getAssociatedType() {
		return ScopeTreeItemDeclareProgressEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemDeclareProgressEventHandler handler) {
		handler.onDeclareProgressRequest(scopeId, progressDescription);
	}
}
