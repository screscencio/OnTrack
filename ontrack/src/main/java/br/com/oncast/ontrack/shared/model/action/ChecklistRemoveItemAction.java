package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistRemoveItemAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	private final UUID itemId;
	private final UUID checklistId;
	private final UUID subjectId;

	public ChecklistRemoveItemAction(final UUID itemId, final UUID checklistId, final UUID subjectId) {
		this.itemId = itemId;
		this.checklistId = checklistId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist checklist = ActionHelper.findChecklist(context, checklistId, subjectId);
		checklist.removeItem(itemId);
		return null;
	}

	@Override
	public UUID getReferenceId() {
		// FIXME Auto-generated catch block
		return null;
	}

}
