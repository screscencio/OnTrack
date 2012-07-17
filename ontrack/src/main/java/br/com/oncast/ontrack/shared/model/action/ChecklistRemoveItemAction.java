package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRemoveItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistRemoveItemActionEntity.class)
public class ChecklistRemoveItemAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID itemId;

	@Element
	private UUID checklistId;

	@Element
	private UUID subjectId;

	protected ChecklistRemoveItemAction() {}

	public ChecklistRemoveItemAction(final UUID subjectId, final UUID checklistId, final UUID itemId) {
		this.subjectId = subjectId;
		this.checklistId = checklistId;
		this.itemId = itemId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist checklist = ActionHelper.findChecklist(subjectId, checklistId, context);
		final ChecklistItem checklistItem = checklist.removeItem(itemId);
		if (checklistItem == null) throw new UnableToCompleteActionException("Unable to remove the given item. The item could not be found.");

		return new ChecklistAddItemAction(subjectId, checklistId, checklistItem);
	}

	@Override
	public UUID getReferenceId() {
		return checklistId;
	}

}