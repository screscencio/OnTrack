package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ScopeTreeFilterByTagEvent extends GwtEvent<ScopeTreeFilterByTagEventHandler> {

	public static Type<ScopeTreeFilterByTagEventHandler> TYPE;
	private final UUID tagId;

	public ScopeTreeFilterByTagEvent(final UUID tagId) {
		this.tagId = tagId;
	}

	public static Type<ScopeTreeFilterByTagEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ScopeTreeFilterByTagEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ScopeTreeFilterByTagEventHandler> getAssociatedType() {
		return ScopeTreeFilterByTagEvent.getType();
	}

	@Override
	protected void dispatch(final ScopeTreeFilterByTagEventHandler handler) {
		handler.onFilterByTagRequested(tagId);
	}

}
