package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

public interface SubjectDetailUpdateEvent {

	UUID getSubjectId();

	void setChecklists(List<Checklist> checklists);

	boolean hasChecklists();

	List<Checklist> getChecklists();

	int getCheckedItemCount();

	int getTotalChecklistItemCount();

	boolean isChecklistComplete();

	String getChecklistCompletitionText();

	void setAnnotations(List<Annotation> annotations);

	int getAnnotationsCount();

	boolean hasAnnotations();

	int getOpenImpedimentsCount();

	boolean hasOpenImpediments();

	void setDescription(Description description);

	boolean hasDescription();

	String getDescriptionText();

	public List<Annotation> getOpenImpediments();

	boolean hasAnyDetails();

}