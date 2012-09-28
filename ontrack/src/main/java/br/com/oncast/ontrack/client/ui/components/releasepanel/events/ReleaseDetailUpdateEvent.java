package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.event.shared.GwtEvent;

public class ReleaseDetailUpdateEvent extends GwtEvent<ReleaseDetailUpdateEventHandler> {

	private static com.google.gwt.event.shared.GwtEvent.Type<ReleaseDetailUpdateEventHandler> TYPE;
	private final Release release;
	private final boolean hasDetails;
	private final boolean hasOpenImpediment;

	public ReleaseDetailUpdateEvent(final Release release, final boolean hasDetails, final boolean hasOpenImpediment) {
		this.release = release;
		this.hasDetails = hasDetails;
		this.hasOpenImpediment = hasOpenImpediment;
	}

	@Override
	protected void dispatch(final ReleaseDetailUpdateEventHandler handler) {
		handler.onReleaseDetailUpdate(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ReleaseDetailUpdateEventHandler> getAssociatedType() {
		return ReleaseDetailUpdateEvent.getType();
	}

	public static Type<ReleaseDetailUpdateEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ReleaseDetailUpdateEventHandler>() : TYPE;
	}

	public Release getTargetRelease() {
		return release;
	}

	public boolean hasDetails() {
		return hasDetails;
	}

	public boolean hasOpenImpediments() {
		return hasOpenImpediment;
	}

}
