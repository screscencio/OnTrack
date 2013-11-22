package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ChecklistUncheckItemAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ChecklistUncheckItem")
@ConvertTo(ChecklistUncheckItemAction.class)
public class ChecklistUncheckItemActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String checklistId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_2)
	private String subjectId;

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_3)
	private String itemId;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(final String checklistId) {
		this.checklistId = checklistId;
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

}
