package br.com.oncast.ontrack.client.services.checklist;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ChecklistService {

	void addChecklist(UUID subjectId, String title);

}
