package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.event.shared.GwtEvent;

public class ReleaseContainerStateChangeEvent extends GwtEvent<ReleaseContainerStateChangeEventHandler> {

	public static Type<ReleaseContainerStateChangeEventHandler> TYPE;
	private final Release release;
	private final boolean isContainerStateOpen;

	public ReleaseContainerStateChangeEvent(final Release release, final boolean isContainerStateOpen) {
		this.release = release;
		this.isContainerStateOpen = isContainerStateOpen;
	}

	public static Type<ReleaseContainerStateChangeEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ReleaseContainerStateChangeEventHandler>() : TYPE;
	}

	@Override
	public Type<ReleaseContainerStateChangeEventHandler> getAssociatedType() {
		return ReleaseContainerStateChangeEvent.getType();
	}

	@Override
	protected void dispatch(final ReleaseContainerStateChangeEventHandler handler) {
		handler.onReleaseContainerStateChange(this);
	}

	public Release getTargetRelease() {
		return release;
	}

	public boolean getTargetContainerState() {
		return isContainerStateOpen;
	}

}
