package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.event.shared.GwtEvent;

public class ReleaseSelectionEvent extends GwtEvent<ReleaseSelectionEventHandler> {

	public static Type<ReleaseSelectionEventHandler> TYPE;
	private final Release release;

	public ReleaseSelectionEvent(final Release release) {
		this.release = release;
	}

	public static Type<ReleaseSelectionEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ReleaseSelectionEventHandler>() : TYPE;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ReleaseSelectionEventHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(final ReleaseSelectionEventHandler handler) {
		handler.onReleaseSelection(this);
	}

	public Release getRelease() {
		return release;
	}

}
