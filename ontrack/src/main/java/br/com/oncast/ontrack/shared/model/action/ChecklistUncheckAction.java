package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistUncheckAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	private final UUID checklistId;

	private final UUID itemId;

	private final UUID subjectId;

	public ChecklistUncheckAction(final UUID subjectId, final UUID checklistId, final UUID itemId) {
		this.checklistId = checklistId;
		this.itemId = itemId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist list = ActionHelper.findChecklist(context, checklistId, subjectId);
		final ChecklistItem item = list.getItem(itemId);
		item.setChecked(false);
		return new ChecklistCheckItemAction(subjectId, checklistId, itemId);
	}

	@Override
	public UUID getReferenceId() {
		// FIXME Auto-generated catch block
		return null;
	}

}
