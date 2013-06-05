package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCheckItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistCheckItemActionEntity.class)
public class ChecklistCheckItemAction implements ChecklistItemAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Element
	private UUID subjectId;

	@Element
	private UUID itemId;

	protected ChecklistCheckItemAction() {}

	public ChecklistCheckItemAction(final UUID subjectId, final UUID checklistId, final UUID itemId) {
		this.checklistId = checklistId;
		this.itemId = itemId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist list = ActionHelper.findChecklist(subjectId, checklistId, context);
		final ChecklistItem item = list.getItem(itemId);
		if (item == null) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CHECKLIST_ITEM_NOT_FOUND);

		item.setChecked(true);
		return new ChecklistUncheckItemAction(subjectId, checklistId, itemId);
	}

	@Override
	public UUID getReferenceId() {
		return itemId;
	}

	@Override
	public UUID getSubjectId() {
		return subjectId;
	}

}
