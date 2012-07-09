package br.com.oncast.ontrack.client.services.checklist;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCheckItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistServiceImpl implements ChecklistService {

	private final ActionExecutionService actionExecutionService;

	public ChecklistServiceImpl(final ActionExecutionService actionExecutionService) {
		this.actionExecutionService = actionExecutionService;
	}

	@Override
	public void addChecklist(final UUID subjectId, final String title) {
		actionExecutionService.onUserActionExecutionRequest(new ChecklistCreateAction(subjectId, title));
	}

	@Override
	public void addCheckistItem(final UUID checklistId, final UUID subjectId, final String itemDescription) {
		actionExecutionService.onUserActionExecutionRequest(new ChecklistAddItemAction(subjectId, checklistId, itemDescription));
	}

	@Override
	public void setItemChecked(final UUID subjectId, final UUID checklistId, final UUID itemId, final Boolean isChecked) {
		actionExecutionService.onUserActionExecutionRequest(new ChecklistCheckItemAction(subjectId, checklistId, itemId));
	}
}
