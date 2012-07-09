package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistAddItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistAddItemActionEntity.class)
public class ChecklistAddItemAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Attribute
	private String itemDescription;

	@Element
	private UUID subjectId;

	@Element
	private UUID itemId;

	protected ChecklistAddItemAction() {}

	public ChecklistAddItemAction(final UUID checklistId, final UUID subjectId, final String itemDescription) {
		this.checklistId = checklistId;
		this.itemId = new UUID();
		this.subjectId = subjectId;
		this.itemDescription = itemDescription;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist list = ActionHelper.findChecklist(context, checklistId, subjectId);
		list.addItem(new ChecklistItem(itemId, itemDescription));

		return new ChecklistRemoveItemAction(itemId, checklistId, subjectId);
	}

	@Override
	public UUID getReferenceId() {
		return checklistId;
	}

}
