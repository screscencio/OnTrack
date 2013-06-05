package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ChecklistItemAction extends ChecklistAction {

	UUID getSubjectId();

}
