package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistUncheckItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Element;

@ConvertTo(ChecklistUncheckItemActionEntity.class)
public class ChecklistUncheckItemAction implements ChecklistItemAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Element
	private UUID itemId;

	@Element
	private UUID subjectId;

	public ChecklistUncheckItemAction() {}

	public ChecklistUncheckItemAction(final UUID subjectId, final UUID checklistId, final UUID itemId) {
		this.checklistId = checklistId;
		this.itemId = itemId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist list = ActionHelper.findChecklist(subjectId, checklistId, context, this);
		final ChecklistItem item = list.getItem(itemId);
		if (item == null) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CHECKLIST_ITEM_NOT_FOUND);
		item.setChecked(false);
		return new ChecklistCheckItemAction(subjectId, checklistId, itemId);
	}

	@Override
	public UUID getReferenceId() {
		return itemId;
	}

	@Override
	public UUID getSubjectId() {
		return subjectId;
	}

	public UUID getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(final UUID checklistId) {
		this.checklistId = checklistId;
	}

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(final UUID itemId) {
		this.itemId = itemId;
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
	}

}
