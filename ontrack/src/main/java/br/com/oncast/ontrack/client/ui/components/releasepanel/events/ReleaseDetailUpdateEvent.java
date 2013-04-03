package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.SubjectDetailUpdateEvent;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.GwtEvent;

public class ReleaseDetailUpdateEvent extends GwtEvent<ReleaseDetailUpdateEventHandler> implements SubjectDetailUpdateEvent {

	private static com.google.gwt.event.shared.GwtEvent.Type<ReleaseDetailUpdateEventHandler> TYPE;

	private final Release release;

	private List<Checklist> checklists;
	private List<Annotation> annotations;
	private Description description;

	public ReleaseDetailUpdateEvent(final Release release) {
		this.release = release;
	}

	public ReleaseDetailUpdateEvent(final Release release, final List<Checklist> checklists, final List<Annotation> annotations,
			final Description description) {
		this.release = release;
		this.checklists = checklists;
		this.annotations = annotations;
		this.description = description;
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

	@Override
	public UUID getSubjectId() {
		return release.getId();
	}

	@Override
	public void setDescription(final Description description) {
		this.description = description;
	}

	@Override
	public void setChecklists(final List<Checklist> checklists) {
		this.checklists = checklists;
	}

	@Override
	public void setAnnotations(final List<Annotation> annotations) {
		this.annotations = annotations;
	}

	@Override
	public List<Checklist> getChecklists() {
		return checklists;
	}

	@Override
	public boolean hasChecklists() {
		return !getChecklists().isEmpty();
	}

	@Override
	public int getAnnotationsCount() {
		return count(AnnotationType.SIMPLE);
	}

	@Override
	public boolean hasAnnotations() {
		return getAnnotationsCount() > 0;
	}

	@Override
	public int getOpenImpedimentsCount() {
		return count(AnnotationType.OPEN_IMPEDIMENT);
	}

	@Override
	public boolean hasOpenImpediments() {
		return getOpenImpedimentsCount() > 0;
	}

	@Override
	public boolean hasDescription() {
		return description != null;
	}

	@Override
	public boolean isChecklistComplete() {
		boolean isComplete = true;
		for (final Checklist c : checklists) {
			isComplete &= c.isComplete();
		}
		return hasChecklists() && isComplete;
	}

	@Override
	public String getChecklistCompletitionText() {
		int total = 0;
		int checked = 0;

		for (final Checklist c : checklists) {
			for (final ChecklistItem item : c.getItems()) {
				total++;
				if (item.isChecked()) checked++;
			}
		}
		return total == 0 ? "" : checked + "/" + total;
	}

	private int count(final AnnotationType type) {
		int count = 0;
		for (final Annotation a : annotations) {
			if (a.getType() == type) count++;
		}
		return count;
	}

}
