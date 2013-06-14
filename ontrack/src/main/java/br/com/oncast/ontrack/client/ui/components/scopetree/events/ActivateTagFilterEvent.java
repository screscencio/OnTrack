package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ActivateTagFilterEvent extends GwtEvent<ActivateTagFilterEventHandler> {

	public static Type<ActivateTagFilterEventHandler> TYPE;
	private final UUID tagId;

	public ActivateTagFilterEvent(final UUID tagId) {
		this.tagId = tagId;
	}

	public static Type<ActivateTagFilterEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ActivateTagFilterEventHandler>();
		}
		return TYPE;
	}

	@Override
	public Type<ActivateTagFilterEventHandler> getAssociatedType() {
		return ActivateTagFilterEvent.getType();
	}

	@Override
	protected void dispatch(final ActivateTagFilterEventHandler handler) {
		handler.onFilterByTagRequested(tagId);
	}

}
