package br.com.oncast.ontrack.client.services.checklist;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ChecklistService {

	void addChecklist(UUID subjectId, String title);

	void addCheckistItem(UUID checklistId, UUID subjectId, String itemDescription);

	void setItemChecked(UUID subjectId, UUID checklistId, UUID itemId, Boolean isChecked);

	void removeItem(UUID subjectId, UUID checklistId, UUID itemId);

	void removeChecklist(UUID subjectId, UUID checklistId);

	void renameChecklist(UUID subjectId, UUID checklistId, String newTitle);

	void editItemDescription(UUID subjectId, UUID checklistId, UUID itemId, String value);

}
