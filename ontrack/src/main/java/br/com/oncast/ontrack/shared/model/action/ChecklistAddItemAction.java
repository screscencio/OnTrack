package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistAddItemActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ChecklistAddItemActionEntity.class)
public class ChecklistAddItemAction implements ChecklistItemAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Attribute
	private String itemDescription;

	@Element
	private UUID subjectId;

	@Element
	private UUID itemId;

	@Attribute
	private boolean checked;

	public ChecklistAddItemAction() {}

	public ChecklistAddItemAction(final UUID subjectId, final UUID checklistId, final String itemDescription) {
		this.checklistId = checklistId;
		this.itemId = new UUID();
		this.subjectId = subjectId;
		this.itemDescription = itemDescription;
		this.checked = false;
	}

	public ChecklistAddItemAction(final UUID subjectId, final UUID checklistId, final ChecklistItem checklistItem) {
		this.subjectId = subjectId;
		this.checklistId = checklistId;
		this.itemId = checklistItem.getId();
		this.itemDescription = checklistItem.getDescription();
		this.checked = checklistItem.isChecked();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist list = ActionHelper.findChecklist(subjectId, checklistId, context, this);
		final ChecklistItem item = new ChecklistItem(itemId, itemDescription);
		item.setChecked(checked);
		list.addItem(item);

		return new ChecklistRemoveItemAction(subjectId, checklistId, itemId);
	}

	@Override
	public UUID getReferenceId() {
		return checklistId;
	}

	@Override
	public UUID getSubjectId() {
		return subjectId;
	}

	public UUID getChecklistId() {
		return checklistId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setChecklistId(final UUID checklistId) {
		this.checklistId = checklistId;
	}

	public void setItemDescription(final String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
	}

	public void setItemId(final UUID itemId) {
		this.itemId = itemId;
	}

	public void setChecked(final boolean checked) {
		this.checked = checked;
	}

	public UUID getItemId() {
		return itemId;
	}

	public boolean getChecked() {
		return checked;
	}

}
