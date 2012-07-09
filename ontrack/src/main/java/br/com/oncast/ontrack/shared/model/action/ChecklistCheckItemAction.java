package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCheckItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistCheckItemActionEntity.class)
public class ChecklistCheckItemAction implements ChecklistAction {

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
		final Checklist list = ActionHelper.findChecklist(context, checklistId, subjectId);
		final ChecklistItem item = list.getItem(itemId);
		item.setChecked(true);
		return new ChecklistUncheckAction(subjectId, checklistId, itemId);
	}

	@Override
	public UUID getReferenceId() {
		return itemId;
	}

}
