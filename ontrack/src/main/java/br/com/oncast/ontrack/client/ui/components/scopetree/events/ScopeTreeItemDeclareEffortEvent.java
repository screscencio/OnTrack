package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeItemDeclareEffortEvent extends GwtEvent<ScopeTreeItemDeclareEffortEventHandler> {

	public static Type<ScopeTreeItemDeclareEffortEventHandler> TYPE;
	private final UUID scopeId;
	private final String effortDescription;

	public ScopeTreeItemDeclareEffortEvent(final UUID scopeId, final String effortDescription) {
		this.scopeId = scopeId;
		this.effortDescription = effortDescription;
	}

	public static Type<ScopeTreeItemDeclareEffortEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeItemDeclareEffortEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeItemDeclareEffortEventHandler> getAssociatedType() {
		return ScopeTreeItemDeclareEffortEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeItemDeclareEffortEventHandler handler) {
		handler.onDeclareEffortRequest(scopeId, effortDescription);
	}
}
