package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.SubjectDetailUpdateEvent;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractSubjectDetailUpdateEvent<T extends HasUUID, H extends EventHandler> extends GwtEvent<H> implements SubjectDetailUpdateEvent {

	private final T subject;

	private List<Annotation> annotations;
	private Description description;

	private List<Checklist> checklists;
	private int checkedItemsCount;
	private int totalChecklistItemsCount;

	public AbstractSubjectDetailUpdateEvent(final T subject) {
		this.subject = subject;
	}

	public AbstractSubjectDetailUpdateEvent(final T subject, final List<Checklist> checklists, final List<Annotation> annotations,
			final Description description) {
		this.subject = subject;
		setChecklists(checklists);
		setAnnotations(annotations);
		setDescription(description);
	}

	public T getTargetSubject() {
		return subject;
	}

	@Override
	public UUID getSubjectId() {
		return subject.getId();
	}

	@Override
	public void setDescription(final Description description) {
		this.description = description;
	}

	@Override
	public void setChecklists(final List<Checklist> checklists) {
		this.checklists = checklists;
		this.checkedItemsCount = -1;
		this.totalChecklistItemsCount = -1;
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
		return getList(AnnotationType.SIMPLE).size();
	}

	@Override
	public boolean hasAnnotations() {
		return getAnnotationsCount() > 0;
	}

	@Override
	public List<Annotation> getOpenImpediments() {
		return getList(AnnotationType.OPEN_IMPEDIMENT);
	}

	@Override
	public int getOpenImpedimentsCount() {
		return getOpenImpediments().size();
	}

	@Override
	public boolean hasOpenImpediments() {
		return !getOpenImpediments().isEmpty();
	}

	@Override
	public String getDescriptionText() {
		return hasDescription() ? description.getDescription() : "";
	}

	@Override
	public boolean hasDescription() {
		return description != null;
	}

	@Override
	public int getCheckedItemCount() {
		updateChecklistCompletition();
		return checkedItemsCount;
	}

	@Override
	public int getTotalChecklistItemCount() {
		updateChecklistCompletition();
		return totalChecklistItemsCount;
	}

	@Override
	public boolean isChecklistComplete() {
		updateChecklistCompletition();
		return hasChecklists() && (checkedItemsCount == totalChecklistItemsCount);
	}

	@Override
	public String getChecklistCompletitionText() {
		updateChecklistCompletition();
		return totalChecklistItemsCount == 0 ? "" : checkedItemsCount + "/" + totalChecklistItemsCount;
	}

	private List<Annotation> getList(final AnnotationType type) {
		final List<Annotation> list = new ArrayList<Annotation>();
		for (final Annotation a : annotations) {
			if (a.getType() == type) list.add(a);
		}
		return list;
	}

	private void updateChecklistCompletition() {
		if (totalChecklistItemsCount != -1) return;

		totalChecklistItemsCount = 0;
		checkedItemsCount = 0;

		for (final Checklist c : checklists) {
			for (final ChecklistItem item : c.getItems()) {
				totalChecklistItemsCount++;
				if (item.isChecked()) checkedItemsCount++;
			}
		}
	}

}
