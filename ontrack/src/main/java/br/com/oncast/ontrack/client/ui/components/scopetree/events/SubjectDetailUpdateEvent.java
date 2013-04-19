package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import java.util.List;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface SubjectDetailUpdateEvent {

	public UUID getSubjectId();

	public void setChecklists(List<Checklist> checklists);

	boolean hasChecklists();

	public List<Checklist> getChecklists();

	public int getCheckedItemCount();

	public int getTotalChecklistItemCount();

	public boolean isChecklistComplete();

	public String getChecklistCompletitionText();

	public void setAnnotations(List<Annotation> annotations);

	public int getAnnotationsCount();

	boolean hasAnnotations();

	public int getOpenImpedimentsCount();

	boolean hasOpenImpediments();

	public void setDescription(Description description);

	public boolean hasDescription();

	public String getDescriptionText();

	public List<Annotation> getOpenImpediments();

}