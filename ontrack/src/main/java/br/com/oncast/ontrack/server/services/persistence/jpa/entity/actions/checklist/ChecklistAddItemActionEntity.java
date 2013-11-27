package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ChecklistAddItemAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ChecklistAddItem")
@ConvertTo(ChecklistAddItemAction.class)
public class ChecklistAddItemActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String checklistId;

	@Column(name = ActionTableColumns.DESCRIPTION_TEXT, length = ActionTableColumns.DESCRIPTION_TEXT_LENGTH)
	private String itemDescription;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String subjectId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String itemId;

	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean checked;

	public String getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(final String checklistId) {
		this.checklistId = checklistId;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(final String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final String subjectId) {
		this.subjectId = subjectId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(final String itemId) {
		this.itemId = itemId;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(final boolean isChecked) {
		this.checked = isChecked;
	}

}
