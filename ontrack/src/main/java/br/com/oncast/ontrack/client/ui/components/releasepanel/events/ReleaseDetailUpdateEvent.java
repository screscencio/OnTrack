package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import java.util.List;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleaseDetailUpdateEvent extends AbstractSubjectDetailUpdateEvent<Release, ReleaseDetailUpdateEventHandler> {

	private static Type<ReleaseDetailUpdateEventHandler> TYPE;

	public ReleaseDetailUpdateEvent(final Release release) {
		super(release);
	}

	public ReleaseDetailUpdateEvent(final Release release, final List<Checklist> checklists, final List<Annotation> annotations,
			final Description description) {
		super(release, checklists, annotations, description);
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

}
