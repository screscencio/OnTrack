package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.web.bindery.event.shared.Event;

public class ReleaseScopeListUpdateEvent extends Event<ReleaseScopeListUpdateEventHandler> {

	public static Type<ReleaseScopeListUpdateEventHandler> TYPE = new Type<ReleaseScopeListUpdateEventHandler>();
	private final Release release;

	public ReleaseScopeListUpdateEvent(final Release release) {
		this.release = release;
	}

	@Override
	public Event.Type<ReleaseScopeListUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final ReleaseScopeListUpdateEventHandler handler) {
		handler.onScopeListInteraction(release);
	}
}
