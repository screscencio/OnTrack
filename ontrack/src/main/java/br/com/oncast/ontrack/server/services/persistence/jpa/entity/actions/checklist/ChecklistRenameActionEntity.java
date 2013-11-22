package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.ChecklistRenameAction;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "ChecklistRename")
@ConvertTo(ChecklistRenameAction.class)
public class ChecklistRenameActionEntity extends ModelActionEntity {

	@Column(name = ActionTableColumns.STRING_1)
	@ConvertUsing(StringToUuidConverter.class)
	private String checklistId;

	@Column(name = ActionTableColumns.STRING_2)
	@ConvertUsing(StringToUuidConverter.class)
	private String subjectId;

	@Column(name = ActionTableColumns.STRING_3)
	private String newTitle;

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

	public String getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(final String newTitle) {
		this.newTitle = newTitle;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final String subjectId) {
		this.subjectId = subjectId;
	}

}
