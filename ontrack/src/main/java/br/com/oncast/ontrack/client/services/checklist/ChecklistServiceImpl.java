package br.com.oncast.ontrack.client.services.checklist;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCheckItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistCreateAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistEditItemDescriptionAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRemoveItemAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistUncheckItemAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistServiceImpl implements ChecklistService {

	private final ActionExecutionService actionExecutionService;

	public ChecklistServiceImpl(final ActionExecutionService actionExecutionService) {
		this.actionExecutionService = actionExecutionService;
	}

	@Override
	public void addChecklist(final UUID subjectId, final String title) {
		doUserAction(new ChecklistCreateAction(subjectId, title));
	}

	@Override
	public void addCheckistItem(final UUID checklistId, final UUID subjectId, final String itemDescription) {
		doUserAction(new ChecklistAddItemAction(subjectId, checklistId, itemDescription));
	}

	@Override
	public void setItemChecked(final UUID subjectId, final UUID checklistId, final UUID itemId, final Boolean isChecked) {
		doUserAction(isChecked ?
				new ChecklistCheckItemAction(subjectId, checklistId, itemId) :
				new ChecklistUncheckItemAction(subjectId, checklistId, itemId));
	}

	@Override
	public void removeItem(final UUID subjectId, final UUID checklistId, final UUID itemId) {
		doUserAction(new ChecklistRemoveItemAction(subjectId, checklistId, itemId));
	}

	@Override
	public void removeChecklist(final UUID subjectId, final UUID checklistId) {
		doUserAction(new ChecklistRemoveAction(subjectId, checklistId));
	}

	@Override
	public void renameChecklist(final UUID subjectId, final UUID checklistId, final String newTitle) {
		doUserAction(new ChecklistRenameAction(subjectId, checklistId, newTitle));
	}

	private void doUserAction(final ChecklistAction action) {
		actionExecutionService.onUserActionExecutionRequest(action);
	}

	@Override
	public void editItemDescription(final UUID subjectId, final UUID checklistId, final UUID itemId, final String newDescription) {
		doUserAction(new ChecklistEditItemDescriptionAction(subjectId, checklistId, itemId, newDescription));
	}
}
