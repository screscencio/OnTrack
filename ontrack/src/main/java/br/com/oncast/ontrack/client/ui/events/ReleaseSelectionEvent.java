package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ReleaseSelectionEvent extends GwtEvent<ReleaseSelectionEventHandler> {

	public static Type<ReleaseSelectionEventHandler> TYPE;
	private final Release release;
	private final UUID projectId;

	public ReleaseSelectionEvent(final Release release, final UUID projectId) {
		this.release = release;
		this.projectId = projectId;
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

	public UUID getProjectId() {
		return projectId;
	}

}
